package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.ChannelRepository;
import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.Channel;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private ChannelRepository channelRepository;

    @GetMapping("channels")
    public String getChannelsList(Model model) {
        List<Channel> channels = channelRepository.findAll();
        model.addAttribute("channels", channels);
        return "home_user";
    }

    @GetMapping("channels/mine")
    public String getMyChannelsList(Model model) {
        List<Channel> channels = channelRepository.findChannelsByOwner();
        model.addAttribute("channels", channels);
        return "home_user";
    }

    @GetMapping("users/add")
    public String getUserForm(Model model) {
        List<Channel> channels = channelRepository.findAll();
        model.addAttribute("channels", channels);
        return "home_user";
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
