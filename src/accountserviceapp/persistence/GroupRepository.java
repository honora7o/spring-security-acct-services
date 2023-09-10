package accountserviceapp.persistence;

import accountserviceapp.business.Group;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Integer> {
    Group findByRole(String role);
}
