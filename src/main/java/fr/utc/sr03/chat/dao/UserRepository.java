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

//    @Modifying(clearAutomatically = true)
//    @Query("update users u set u.firstname = ?1 where u.id = ?2")
//    public int setFirstname(String firstName, long id);

    User getById(Long id);

    User getByMail(String mail);

    User findFirstByMailAndPassword(String mail, String password);
}
