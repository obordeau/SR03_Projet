package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserPaging extends PagingAndSortingRepository<User, Long> {
}
