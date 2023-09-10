package accountserviceapp.presentation;

import accountserviceapp.business.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountRestController {
    private final UserService userService;
    private final SalaryService salaryService;
    private final LoggingService loggingService;
    private final EventLogService eventLogService;
    private final AccountantService accountantService;
    private final AdministratorService administratorService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public AccountRestController(UserService userService,
                                 SalaryService salaryService,
                                 LoggingService loggingService,
                                 EventLogService eventLogService,
                                 AccountantService accountantService,
                                 AdministratorService administratorService) {
        this.userService = userService;
        this.salaryService = salaryService;
        this.loggingService = loggingService;
        this.eventLogService = eventLogService;
        this.accountantService = accountantService;
        this.administratorService = administratorService;
    }

    @PostMapping("api/auth/signup")
    public ResponseEntity<?> signUpUser(@RequestBody @Valid User userToSignUp,
                                        BindingResult bindingResult) {

        userService.signUpUser(userToSignUp, bindingResult);
        loggingService.logCurrEvent(new EventLog(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "CREATE_USER",
                userService.getCurrAuthUser(),
                userToSignUp.getEmail().toLowerCase(),
                httpServletRequest.getRequestURI()));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserResponse(userToSignUp));
    }

    @GetMapping("api/empl/payment")
    public ResponseEntity<?> getEmplPayment(@RequestParam(required = false) String period,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        ResponseEntity<?> responseEntity = salaryService.getEmplPayment(period, userDetails);
        return responseEntity;
    }

    @PostMapping("api/auth/changepass")
    public ResponseEntity<?> changeUserPassword(@RequestBody PasswordChangeRequest passwordChangeRequest,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        String currAuthUser = userDetails.getUsername().toLowerCase();

        loggingService.logCurrEvent(new EventLog(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "CHANGE_PASSWORD",
                currAuthUser,
                currAuthUser,
                httpServletRequest.getRequestURI()));

        return userService.changeUserPassword(userDetails, passwordChangeRequest);
    }

    @PostMapping("api/acct/payments")
    public ResponseEntity<?> uploadPayrolls(@RequestBody ArrayList<PaymentDTO> paymentDTOList) {
        return accountantService.uploadPayrolls(paymentDTOList);
    }

    @PutMapping("api/acct/payments")
    public ResponseEntity<?> uploadPayroll(@RequestBody PaymentDTO paymentDTO) {
        return accountantService.uploadPayroll(paymentDTO);
    }

    @GetMapping("api/admin/user/")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsersInDB();

        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userResponses);
    }

    @DeleteMapping("api/admin/user/{userEmail}")
    public ResponseEntity<?> deleteUserByEmail(@PathVariable String userEmail) {
        administratorService.deleteUserByEmail(userEmail);

        loggingService.logCurrEvent(new EventLog(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "DELETE_USER",
                userService.getCurrAuthUser(),
                userEmail,
                httpServletRequest.getRequestURI())
        );

        return new ResponseEntity(Map.of("status", "Deleted successfully!",
                "user", userEmail), HttpStatus.OK);
    }

    @PutMapping("api/admin/user/role")
    public ResponseEntity<?> manageUserRoles(@RequestBody RoleManageRequest roleManageRequest) {
        String userToManageEmail = roleManageRequest.getEmail();
        String userToManageRole = roleManageRequest.getRole();
        String operation = roleManageRequest.getOperation();

        administratorService.manageUserRoles(userToManageEmail, userToManageRole, operation);

        String formattedOperation = Character.toUpperCase(operation.charAt(0)) + operation.substring(1).toLowerCase();
        String preposition = operation.equals("GRANT") ? "to" : "from";
        loggingService.logCurrEvent(new EventLog(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                roleManageRequest.getOperation() + "_ROLE",
                userService.getCurrAuthUser(),
                formattedOperation + " role " + roleManageRequest.getRole() + " " + preposition + " " + roleManageRequest.getEmail().toLowerCase(),
                httpServletRequest.getRequestURI()
        ));

        User managedUser = userService.getUserByEmail(userToManageEmail);
        return ResponseEntity.ok(new UserResponse(managedUser));
    }

    @PutMapping("api/admin/user/access")
    public ResponseEntity<?> toggleUserAccess(@RequestBody UserAccessRequest userAccessRequest) {
        String userToManageEmail = userAccessRequest.getUser().toLowerCase();
        String operation = userAccessRequest.getOperation();

        administratorService.toggleUserAccess(userToManageEmail, operation);

        String formattedOperation = Character.toUpperCase(operation.charAt(0)) + operation.substring(1).toLowerCase();
        loggingService.logCurrEvent(new EventLog(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                operation + "_USER",
                userService.getCurrAuthUser(),
                formattedOperation + " user " + userToManageEmail,
                httpServletRequest.getRequestURI())
        );

        return new ResponseEntity(Map.of("status",
                "User " + userToManageEmail + " " + operation.toLowerCase() + "ed!"), HttpStatus.OK);
    }

    @GetMapping("api/security/events/")
    public ResponseEntity<?> getSecurityLogs() {
        List<EventLog> eventLogs = (eventLogService.getAllEventLogs() == null) ? List.of() : eventLogService.getAllEventLogs();
        return ResponseEntity.ok(eventLogs);
    }
}
