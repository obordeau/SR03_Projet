package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.regex.*;
import java.util.*;

import java.util.List;

@Controller
@SessionAttributes("user")
@RequestMapping("login")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping
    public String postLogin(@ModelAttribute("user") User user, BindingResult result, Model model, WebRequest request) {
        //TODO verif
        User currentUser =
                userRepository.getByMailAndPassword(user.getMail(),user.getPassword());
        if (currentUser != null) {
            request.setAttribute("connected", true, WebRequest.SCOPE_SESSION);
            request.setAttribute("firstName", currentUser.getFirstName(), WebRequest.SCOPE_SESSION);
            request.setAttribute("lastName", currentUser.getLastName(), WebRequest.SCOPE_SESSION);
            request.setAttribute("mail", currentUser.getMail(), WebRequest.SCOPE_SESSION);
            request.setAttribute("id", currentUser.getId(), WebRequest.SCOPE_SESSION);
            request.setAttribute("admin", currentUser.isAdmin(), WebRequest.SCOPE_SESSION);
            request.setAttribute("active", currentUser.isActive(), WebRequest.SCOPE_SESSION);
            if (currentUser.isAdmin() == 1) {
                List<User> userList = userRepository.findAll();
                model.addAttribute("usersList", userList);
                return "home_admin";
            } else {
                return "home_user";
            }
        }
        return "login";
    }
}
