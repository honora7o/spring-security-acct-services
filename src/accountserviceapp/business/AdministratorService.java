package accountserviceapp.business;

import accountserviceapp.exceptions.CustomExceptions;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService {
    private final UserService userService;

    public AdministratorService(UserService userService) {
        this.userService = userService;
    }

    private void validateUserDeletion(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new CustomExceptions.InvalidRequestException();
        }

        User userToDelete = userService.getUserByEmail(userEmail);
        if (userToDelete == null) {
            throw new CustomExceptions.UserNotFoundException();
        }

        if (userToDelete.isAdministrator()) {
            throw new CustomExceptions.AdminDeletionException();
        }
    }

    @Transactional
    public void deleteUserByEmail(String userEmail) {
        validateUserDeletion(userEmail);
        userService.deleteUserByEmail(userEmail);
    }

    private void validateUserRoleManagement(String email, String role, String operation) {
        User user = userService.getUserByEmail(email);

        if (user == null) {
            throw new CustomExceptions.UserNotFoundException();
        }

        if (role == null || !role.matches("ADMINISTRATOR|ACCOUNTANT|USER|AUDITOR")) {
            throw new CustomExceptions.RoleNotFoundException();
        }

        String formattedRole = "ROLE_" + role;

        if (!user.getRoleStrings().contains(formattedRole) && operation.equals("REMOVE")) {
            throw new CustomExceptions.UserNotOfRoleException();
        }

        if (user.isAdministrator() && operation.equals("REMOVE")) {
            throw new CustomExceptions.AdminDeletionException();
        }

        if (user.getRoleStrings().size() == 1 && operation.equals("REMOVE")) {
            throw new CustomExceptions.UserMinimumRoleException();
        }
    }

    @Transactional
    public void manageUserRoles(String email, String role, String operation) {
        validateUserRoleManagement(email, role, operation);

        String formattedRole = "ROLE_" + role;

        User user = userService.getUserByEmail(email);

        if (operation.equals("GRANT")) {
            userService.setUserGroup(user, formattedRole);
        } else if (operation.equals("REMOVE")) {
            userService.removeUserGroup(user, formattedRole);
        }
    }

    private void validateToggleUserAccess(String userEmail, String operation) {
        User user = userService.getUserByEmail(userEmail);

        if (user == null) {
            throw new CustomExceptions.UserNotFoundException();
        }

        if (user.isAdministrator()) {
            throw new CustomExceptions.AdministratorAccessLockException();
        }

        if (!operation.matches("LOCK|UNLOCK")) {
            throw new CustomExceptions.InvalidAccessManageOperationException();
        }
    }

    @Transactional
    public void toggleUserAccess(String userEmail, String operation) {
        validateToggleUserAccess(userEmail, operation);

        User user = userService.getUserByEmail(userEmail);

        if (operation.equals("LOCK")) {
            userService.lock(user);
        } else if (operation.equals("UNLOCK")) {
            userService.unlock(user);
        }
    }
}
