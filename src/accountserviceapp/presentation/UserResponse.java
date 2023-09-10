package accountserviceapp.presentation;

import accountserviceapp.business.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String name;
    private String lastname;
    private String email;
    private Set<String> roles;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getFirstName();
        this.lastname = user.getLastName();
        this.email = user.getEmail().toLowerCase();
        this.roles = new TreeSet<>(user.getRoleStrings());
    }
}
