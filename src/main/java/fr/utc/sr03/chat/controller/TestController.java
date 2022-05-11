package fr.utc.sr03.chat.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.userdetails.UserDetails;


@Controller
public class TestController {
	@Autowired
    private UserRepository userRepository;

//    @GetMapping("/test")
//     // Pour faire sans template html
//    public String test() {
//    	List<User> users = userRepository.findAll();
//        users.forEach(user -> {
//            System.out.println(user.getFirstName() + " : " + user.isAdmin());
//        });
//        return "home_admin";
//    }
    @GetMapping("/test")
    @ResponseBody
    public String adminPage(Model model, Principal principal) {
        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
        User user = userRepository.getByMail(loginedUser.getUsername());
        model.addAttribute("user", user);

        return "ok";
    }
}
