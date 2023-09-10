package accountserviceapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomExceptions {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "User exist!")
    public static class UserExistException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password length must be 12 chars minimum!")
    public static class InvalidPasswordException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The password is in the hacker's database!")
    public static class BreachedPasswordException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The passwords must be different!")
    public static class EqualPasswordException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid payment format request body.")
    public static class InvalidPaymentFormatException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Cannot add more than one payment to the same period!")
    public static class DuplicatePaymentException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid employee email!")
    public static class InvalidEmployeeEmailException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid period!")
    public static class InvalidPeriodException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found!")
    public static class UserNotFoundException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Can't remove ADMINISTRATOR role!")
    public static class AdminDeletionException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Role not found!")
    public static class RoleNotFoundException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The user does not have a role!")
    public static class UserNotOfRoleException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The user must have at least one role!")
    public static class UserMinimumRoleException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The user cannot combine administrative and business roles!")
    public static class InvalidRoleCombinationException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Can't lock the ADMINISTRATOR!")
    public static class AdministratorAccessLockException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid operation!")
    public static class InvalidAccessManageOperationException extends RuntimeException {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid or missing user params!!")
    public static class InvalidRequestException extends RuntimeException {}
}
