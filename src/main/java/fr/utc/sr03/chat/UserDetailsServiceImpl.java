package fr.utc.sr03.chat;

import java.util.ArrayList;
import java.util.List;

import fr.utc.sr03.chat.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        fr.utc.sr03.chat.model.User appUser = this.userRepository.getByMail(mail);

        if (appUser == null) {
            throw new UsernameNotFoundException("User " + mail + " was not found in the database");
        }

        Integer role = appUser.isAdmin();

        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        if (role == 1) {
            GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
            grantList.add(authority);
        }
        if (role == 0) {
            GrantedAuthority authority = new SimpleGrantedAuthority("USER");
            grantList.add(authority);
        }

        UserDetails userDetails = (UserDetails) new User(appUser.getMail(), //
                appUser.getPassword(), grantList);
        return userDetails;
    }

}