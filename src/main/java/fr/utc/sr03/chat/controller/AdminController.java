package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("users")
    public String getUserList(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "home_admin";
    }

    @GetMapping("users/desactivated")
    public String getDesactivatedUsers(Model model) {
        List<User> users = userRepository.findUsersByActiveIs(0);
        model.addAttribute("users", users);
        return "home_admin";
    }

    @GetMapping("users/add")
    public String getUserForm(Model model) {
        model.addAttribute("newUser", new User());
        return "add_user";
    }

    @PostMapping("users/add")
    public String addUser(@ModelAttribute User newUser, Model model) {
        if (!userRepository.findByMail(newUser.getMail()).isEmpty()) {
            System.out.println("Email déja affecte.");
            return "add_user";
        }
        newUser.setActive(0);
        newUser.setAdmin(1);
        userRepository.save(newUser);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "home_admin";
    }

    @RequestMapping ("users/delete/{userId}")
    public String deleteUser(@PathVariable long userId, Model model) {
        userRepository.deleteUserById(userId);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "home_admin";
    }
    @GetMapping("modifySelf")
    public String getUserInformation(Model model)
    {
        model.addAttribute("user", new User());
        return "modif_user";
    }

    @PostMapping("modifySelf")
    public String modifyUserInformation(@ModelAttribute User user, Model model, WebRequest request) {
        User currentUser = (User)request.getAttribute("user", WebRequest.SCOPE_SESSION);
        currentUser.setMail(user.getMail());
        currentUser.setLastName(user.getLastName());
        currentUser.setFirstName(user.getFirstName());
        request.setAttribute("user", currentUser, WebRequest.SCOPE_SESSION);
        userRepository.save(currentUser);
        return "home_admin";
    }

    @GetMapping("deconnect")
    public String deconnexion(WebRequest request)
    {
        return "modif_user";
    }
}
