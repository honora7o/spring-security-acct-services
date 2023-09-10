package accountserviceapp.business;

import accountserviceapp.exceptions.CustomExceptions;
import accountserviceapp.persistence.GroupRepository;
import accountserviceapp.persistence.UserRepository;
import accountserviceapp.presentation.PasswordChangeRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;

    public static final int MAX_FAILED_ATTEMPTS = 5;

    private final Set<String> breachedPasswords = new HashSet<>(Arrays.asList(
            "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
    ));

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmailIgnoreCase(username);
        if (user == null) {
            throw new UsernameNotFoundException("Not found");
        }

        return new AccountAdapter(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmailIgnoreCase(email);
    }

    private void saveUserToDB(User userToSave) {
        userRepository.save(userToSave);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private boolean isPasswordBreached(String password) {
        return breachedPasswords.contains(password);
    }

    @Transactional
    private void changeUserPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    private boolean arePasswordsEqual(String oldPassword, String newPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }

    private boolean isFirstUser() {
        return userRepository.isUserTableEmpty();
    }

    public void setUserGroup(User user, String role) {
        Group groupToAdd = groupRepository.findByRole(role);

        boolean hasAdministrativeGroup = user.hasGroupOfType("administrative");
        boolean hasBusinessGroup = user.hasGroupOfType("business");

        if ((groupToAdd.getType().equals("administrative") && hasBusinessGroup)
                || (groupToAdd.getType().equals("business") && hasAdministrativeGroup)) {
            throw new CustomExceptions.InvalidRoleCombinationException();
        }

        user.addUserGroup(groupToAdd);
        userRepository.save(user);
    }

    public void removeUserGroup(User user, String role) {
        Group group = groupRepository.findByRole(role);
        user.removeUserGroup(group);
        userRepository.save(user);
    }

    public List<User> getAllUsersInDB() {
        return userRepository.findAll();
    }

    public void deleteUserByEmail(String email) {
        User userToDelete = userRepository.findUserByEmailIgnoreCase(email);
        userRepository.delete(userToDelete);
    }

    @Transactional
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(newFailAttempts, user.getEmail());
    }

    @Transactional
    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(0, email);
    }

    @Transactional
    public void lock(User user) {
        if (!user.isAdministrator()) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }

    @Transactional
    public void unlock(User user) {
        user.setAccountNonLocked(true);
        this.resetFailedAttempts(user.getEmail());
        userRepository.save(user);
    }

    public String getCurrAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication.getName().equals("anonymousUser")) ? "Anonymous" : authentication.getName().toLowerCase();
    }

    @Transactional
    public void signUpUser(User userToSignUp, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomExceptions.InvalidRequestException();
        }

        String userSubmittedPassword = userToSignUp.getPassword();

        if (userSubmittedPassword.length() < 12) {
            throw new CustomExceptions.InvalidPasswordException();
        }

        if (isPasswordBreached(userSubmittedPassword)) {
            throw new CustomExceptions.BreachedPasswordException();
        }

        userToSignUp.setPassword(encodePassword(userSubmittedPassword));
        userToSignUp.setAccountNonLocked(true);
        userToSignUp.setFailedAttempt(0);

        if (emailExists(userToSignUp.getEmail())) {
            throw new CustomExceptions.UserExistException();
        }

        if (isFirstUser()) {
            setUserGroup(userToSignUp, "ROLE_ADMINISTRATOR");
        } else {
            setUserGroup(userToSignUp, "ROLE_USER");
        }

        saveUserToDB(userToSignUp);
    }

    @Transactional
    public ResponseEntity<?> changeUserPassword(UserDetails userDetails, PasswordChangeRequest passwordChangeRequest) {
        String newPassword = passwordChangeRequest.getNewPassword();
        String currPasswordHash = userDetails.getPassword();

        if (newPassword.length() < 12) {
            throw new CustomExceptions.InvalidPasswordException();
        }

        if (isPasswordBreached(newPassword)) {
            throw new CustomExceptions.BreachedPasswordException();
        }

        if (arePasswordsEqual(currPasswordHash, newPassword)) {
            throw new CustomExceptions.EqualPasswordException();
        }

        String currUserEmail = userDetails.getUsername().toLowerCase();
        String encodedNewPassword = encodePassword(newPassword);

        User user = getUserByEmail(currUserEmail);
        changeUserPassword(user, encodedNewPassword);

        return ResponseEntity.ok(Map.of("status", "The password has been updated successfully", "email", currUserEmail.toLowerCase()));
    }
}
