package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserPaging extends PagingAndSortingRepository<User, Long> {
    Page<User> findUsersByActiveIs(int value, Pageable pageable);
}
