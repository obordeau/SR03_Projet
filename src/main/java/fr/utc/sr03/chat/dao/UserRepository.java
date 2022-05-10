package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User getByMailAndPassword(String mail, String password);

    List<User> findAll();

    List<User> findByActiveEquals(int active);

    List<User> findByMail(String mail);

    @Transactional
    int deleteUserById(long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE users SET firstname= :firstname WHERE id= :id")
    static void updateFirstname(@Param(value = "firstname") String firstname, @Param(value = "id") long id) {
    }

    User getById(Long id);

    User getByMail(String mail);

    User findFirstByMailAndPassword(String mail, String password);
}
