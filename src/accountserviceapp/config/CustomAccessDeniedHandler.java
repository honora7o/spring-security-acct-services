package accountserviceapp.config;

import accountserviceapp.business.EventLog;
import accountserviceapp.business.LoggingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final LoggingService loggingService;

    @Autowired
    public CustomAccessDeniedHandler(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        loggingService.logCurrEvent(new EventLog(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "ACCESS_DENIED",
                request.getUserPrincipal().getName().toLowerCase(),
                request.getRequestURI(),
                request.getRequestURI())
        );
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }
}
