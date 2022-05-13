package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.Channel;
import fr.utc.sr03.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    User getByMailAndPassword(String mail, String password);

    List<Channel> findAll();

    List<Channel> findChannelsByOwner(long ownerID);

    int deleteChannelById(long id);

    Channel getById(Long id);

}
