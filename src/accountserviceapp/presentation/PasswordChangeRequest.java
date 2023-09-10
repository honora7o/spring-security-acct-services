package accountserviceapp.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {
    @JsonProperty("new_password")
    private String newPassword;
}
