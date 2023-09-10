package accountserviceapp.persistence;

import accountserviceapp.business.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserByEmailIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT COUNT(u) FROM User u")
    long countUsers();
    default boolean isUserTableEmpty() {
        return countUsers() == 0;
    }

    List<User> findAll();

    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    @Modifying
    public void updateFailedAttempts(int failAttempts, String email);
}
