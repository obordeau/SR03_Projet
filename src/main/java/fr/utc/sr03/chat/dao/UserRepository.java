package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User getByMailAndPassword(String mail, String password);
    List<User> findById(long id);

    User findFirstByMailAndPassword(String mail, String password);
}
