package accountserviceapp.config;

import accountserviceapp.business.EventLog;
import accountserviceapp.business.LoggingService;
import accountserviceapp.business.User;
import accountserviceapp.business.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final LoggingService loggingService;

    @Autowired
    private UserService userService;

    public RestAuthenticationEntryPoint(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] splitCredentials = credentials.split(":", 2);

            if (splitCredentials.length == 2) {
                String username = splitCredentials[0].toLowerCase();
                User currUser = userService.getUserByEmail(username);

                if (currUser != null && currUser.isAccountNonLocked()) {
                    if (currUser.getFailedAttempt() < userService.MAX_FAILED_ATTEMPTS - 1) {
                        userService.increaseFailedAttempts(currUser);
                        loggingService.logCurrEvent(new EventLog(
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                "LOGIN_FAILED",
                                username,
                                request.getRequestURI(),
                                request.getRequestURI())
                        );
                    } else {
                        userService.increaseFailedAttempts(currUser);
                        userService.lock(currUser);
                        loggingService.logCurrEvent(new EventLog(
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                "LOGIN_FAILED",
                                username,
                                request.getRequestURI(),
                                request.getRequestURI())
                        );
                        loggingService.logCurrEvent(new EventLog(
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                "BRUTE_FORCE",
                                username,
                                request.getRequestURI(),
                                request.getRequestURI())
                        );
                        loggingService.logCurrEvent(new EventLog(
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                "LOCK_USER",
                                username,
                                "Lock user " + username,
                                request.getRequestURI())
                        );
                    }
                } else if (currUser == null) {
                    loggingService.logCurrEvent(new EventLog(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            "LOGIN_FAILED",
                            username,
                            request.getRequestURI(),
                            request.getRequestURI())
                    );
                }
            }
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}