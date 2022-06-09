package fr.utc.sr03.chat;

import fr.utc.sr03.chat.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    Page<User> findPaginated(int pageNo, int pageSize);
}
