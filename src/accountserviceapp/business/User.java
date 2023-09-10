package accountserviceapp.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.SortNatural;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users_db")
public class User {
    @Id
    @GeneratedValue
    private int id;

    @JsonProperty("name")
    @NotBlank
    private String firstName;

    @JsonProperty("lastname")
    @NotBlank
    private String lastName;

    @JsonProperty("email")
    @Pattern(regexp = "\\w+(@acme.com)$")
    @NotBlank
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String password;

    @Column(name = "account_non_locked")
    @JsonIgnore
    private boolean accountNonLocked;

    @Column(name = "failed_attempt")
    @JsonIgnore
    private int failedAttempt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    @JsonProperty("roles")
    @JsonIgnoreProperties("users")
    @SortNatural
    private Set<Group> userGroups = new TreeSet<>();

    public void addUserGroup(Group group) {
        userGroups.add(group);
    }

    public void removeUserGroup(Group group) {
        userGroups.remove(group);
    }

    public Set<String> getRoleStrings() {
        return userGroups.stream()
                .map(Group::getRole)
                .collect(Collectors.toSet());
    }

    public boolean isAdministrator() {
        Set<String> roles = getRoleStrings();
        return roles.contains("ROLE_ADMINISTRATOR");
    }

    public boolean hasGroupOfType(String type) {
        return userGroups.stream()
                .anyMatch(group -> group.getType().equals(type));
    }
}
