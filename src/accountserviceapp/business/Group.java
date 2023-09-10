package accountserviceapp.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "principle_groups")
public class Group implements Comparable<Group> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @Column(unique = true, nullable = false)
    private String role;

    @JsonIgnore
    private String type;

    @ManyToMany(mappedBy = "userGroups")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public Group(String role) {
        this.role = role;
        this.type = role.matches("ROLE_ADMINISTRATOR") ? "administrative" : "business";
    }

    @Override
    public int compareTo(Group otherGroup) {
        return this.role.compareTo(otherGroup.role);
    }
}
