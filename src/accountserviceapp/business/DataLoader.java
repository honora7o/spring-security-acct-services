package accountserviceapp.business;

import accountserviceapp.persistence.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {
    private final GroupRepository groupRepository;

    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            groupRepository.save(new Group("ROLE_ACCOUNTANT"));
            groupRepository.save(new Group("ROLE_ADMINISTRATOR"));
            groupRepository.save(new Group("ROLE_AUDITOR"));
            groupRepository.save(new Group("ROLE_USER"));
        } catch (Exception e) {

        }
    }
}
