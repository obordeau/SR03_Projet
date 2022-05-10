package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("users")
    public String getUserList(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user_list";
    }

    @GetMapping("users/add")
    public String getUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add_user";
    }

    @PostMapping("users/add")
    public String addUser(@ModelAttribute User user, Model model) {
        if (!userRepository.findByMail(user.getMail()).isEmpty()) {
            System.out.println("Email déja affecte.");
            return "add_user";
        }
        user.setActive(0);
        user.setAdmin(1);
        userRepository.save(user);
        return "home_admin";
    }
}
