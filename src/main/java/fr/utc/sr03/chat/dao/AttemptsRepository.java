package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.Attempts;
import fr.utc.sr03.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptsRepository extends JpaRepository<Attempts, Long> {
    Attempts findFirstByUser(User user);
}
