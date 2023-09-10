package accountserviceapp.config;

import accountserviceapp.business.User;
import accountserviceapp.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if (authentication != null) {
            User currUser = userService.getUserByEmail(authentication.getName());
            if (currUser.getFailedAttempt() > 0 && currUser.isAccountNonLocked()) {
                userService.resetFailedAttempts(authentication.getName());
            }
        }
    }
}
