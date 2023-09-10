package accountserviceapp.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleManageRequest {
    @JsonProperty("user")
    private String email;

    private String role;
    private String operation;
}
