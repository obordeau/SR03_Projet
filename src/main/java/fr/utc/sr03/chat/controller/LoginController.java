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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.*;
import java.util.*;

import java.util.List;

@Controller
@RequestMapping("login")
public class LoginController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;

    @GetMapping
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping
    public String postLogin(@ModelAttribute("user") User user, Model model, WebRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // TODO verif
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(user.getPassword().getBytes("utf8"));
        String hash = String.format("%0128x", new BigInteger(1, digest.digest()));
        user.setPassword(hash);
        User currentUser = userRepository.getByMailAndPassword(user.getMail(), user.getPassword());
        if (currentUser != null) {
            request.setAttribute("user", currentUser, WebRequest.SCOPE_SESSION);
            request.setAttribute("connected", true, WebRequest.SCOPE_SESSION);
            if (currentUser.isAdmin() == 1) {
                List<User> users = userRepository.findAll();
                model.addAttribute("users", users);
                return "home_admin";
            } else {
                System.out.println("L'utilisateur n'est pas admin");
//                List<Channel> channels = channelRepository.findAll();
//                List owners = new ArrayList<>();
//                for (int i = 0; i < channels.size(); i++) {
//                    owners.add(userRepository.getById((long) channels.get(i).getOwner()).getFirstName());
//                }
//                model.addAttribute("channels", channels);
//                model.addAttribute("owners", owners);
//                return "home_user";
            }
        }
        return "login";
    }
}
