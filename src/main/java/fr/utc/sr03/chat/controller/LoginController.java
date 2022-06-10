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
        model.addAttribute("color", "color : #999999");
        model.addAttribute("border", "border: 5px solid #efefef");
        model.addAttribute("alerte", "");
        model.addAttribute("coloralerte", "color : #aaaaaa");
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
            if (currentUser.isAdmin() == 1) {
                request.setAttribute("user", currentUser, WebRequest.SCOPE_SESSION);
                request.setAttribute("connected", true, WebRequest.SCOPE_SESSION);
                return "redirect:/admin/0";
            } else {
                model.addAttribute("color", "color : #e74c3c");
                model.addAttribute("border", "border: 5px solid #e74c3c");
                model.addAttribute("user", new User());
                model.addAttribute("alerte", "L'utilisateur n'est pas admin.");
                model.addAttribute("coloralerte", "color : #e74c3c");
                return "login";
            }
        }
        model.addAttribute("alerte", "L'utilisateur ou le mot de passe n'est pas valide.");
        model.addAttribute("coloralerte", "color : #e74c3c");
        model.addAttribute("color", "color : #e74c3c");
        model.addAttribute("border", "border: 5px solid #e74c3c");
        model.addAttribute("user", new User());
        return "login";
    }
}
