package accountserviceapp.presentation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAccessRequest {
    private String user;
    private String operation;
}
