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
import java.util.regex.*;
import java.util.*;

import java.util.List;

@Controller
@RequestMapping("login")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    public boolean isValidEmail(String email) {
        //Regular Expression
        String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        //Create instance of matcher
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @PostMapping
    public String postLogin(@ModelAttribute User user, Model model) {
        //TODO verif login
        System.out.println("===> mail = " + user.getMail());
        System.out.println("===> password = " + user.getPassword());

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        return "home_admin";
    }
}
