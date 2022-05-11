package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("home")
    public String getUserList(Model model, Principal principal) {
        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
        User user = userRepository.getByMail(loginedUser.getUsername());
        model.addAttribute("user", user);
        List<User> userList = userRepository.findAll();
        model.addAttribute("usersList", userList);
        return "home_admin";
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
        List<User> userList = userRepository.findAll();
        model.addAttribute("usersList", userList);
        return "home_admin";
    }

    @RequestMapping ("users/delete/{userId}")
    public String deleteUser(@PathVariable long userId, Model model) {
        userRepository.deleteUserById(userId);
        List<User> userList = userRepository.findAll();
        model.addAttribute("usersList", userList);
        return "home_admin";
    }
//    @GetMapping("modifySelf/{id}")
//    public String getUserInformation(@PathVariable Long id, Model model)
//    {
//        User user = userRepository.getById(id);
//        System.out.println(user.getFirstName());
//        model.addAttribute("user", user);
//        return "modif_user";
//    }
//
//    @PostMapping("modifySelf")
//    public String modifyUserInformation(@ModelAttribute User user, Model model) {
//        System.out.println(user.getFirstName());
//        //user.updateUser(user.getId(), user.getFirstName());
//        return "home_admin";
//    }
}
