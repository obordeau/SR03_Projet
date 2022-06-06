package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.Channel;
import fr.utc.sr03.chat.model.Guests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GuestsRepository extends JpaRepository<Guests, Long> {
    List<Guests> findByChannel(int channel);
    List<Guests> findByUser(int user);
    @Transactional
    int deleteGuestsByUserAndChannel(int user, int channel);

}
