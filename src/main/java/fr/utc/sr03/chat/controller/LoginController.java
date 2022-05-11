package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.regex.*;
import java.util.*;

import java.util.List;

@Controller
@RequestMapping("login")
public class LoginController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public String getLogin(Model model, Principal principal) {
        if (principal != null) {
            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
            if (loginedUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
                return "/admin/home";
            }else{
                return "user";
            }
        }
        model.addAttribute("user", new User());
        return "login";
    }

//    @GetMapping
//    public String getLogin(Model model, Principal principal) {
//        if (principal != null) {
//            UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
//            User user = userRepository.getByMail(loginedUser.getUsername());
//            model.addAttribute("user", user);
//            System.out.println(loginedUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER")));
//            if (user.isAdmin() == 1) {
//                List<User> userList = userRepository.findAll();
//                model.addAttribute("usersList", userList);
//                return "home_admin";
//            }else{
//                return "home_user";
//            }
//        }
//        model.addAttribute("user", new User());
//        return "login";
//    }

    @PostMapping
    public String postLogin(@ModelAttribute User user, Model model) {
        //TODO verif
        User currentUser =
                userRepository.getByMailAndPassword(user.getMail(),user.getPassword());
        model.addAttribute("user", currentUser);

        if (currentUser.isAdmin() == 1) {
            List<User> userList = userRepository.findAll();
            model.addAttribute("usersList", userList);
            return "home_admin";
        }

        return "home_user";
    }
}
