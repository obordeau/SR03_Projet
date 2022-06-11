package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.AttemptsRepository;
import fr.utc.sr03.chat.dao.ChannelRepository;
import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.Attempts;
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
    @Autowired
    private AttemptsRepository attemptsRepository;

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
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(user.getPassword().getBytes("utf8"));
        String hash = String.format("%0128x", new BigInteger(1, digest.digest()));
        user.setPassword(hash);
        User userTest = userRepository.getByMail(user.getMail());
        Attempts att = attemptsRepository.findFirstByUser(userTest);
        if (att.getAccount_blocked() == 0) {
            att.setNumber_attempts(att.getNumber_attempts()+1);
            attemptsRepository.save(att);
            if (att.getNumber_attempts() < 5) {
                User currentUser = userRepository.getByMailAndPassword(user.getMail(), user.getPassword());
                if (currentUser != null) {
                    att.setNumber_attempts(0);
                    attemptsRepository.save(att);
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
            } else {
                att.setAccount_blocked(1);
                attemptsRepository.save(att);
                model.addAttribute("color", "color : #e74c3c");
                model.addAttribute("border", "border: 5px solid #e74c3c");
                model.addAttribute("user", new User());
                model.addAttribute("alerte", "Vous venez d'essayer de vous connecter 5 fois, votre compte est bloqué.");
                model.addAttribute("coloralerte", "color : #e74c3c");
                return "login";
            }
        } else {
            model.addAttribute("color", "color : #e74c3c");
            model.addAttribute("border", "border: 5px solid #e74c3c");
            model.addAttribute("user", new User());
            model.addAttribute("alerte", "L'utilisateur est bloqué, veuillez contacter un admin.");
            model.addAttribute("coloralerte", "color : #e74c3c");
            return "login";
        }
        model.addAttribute("alerte", "L'utilisateur ou le mot de passe n'est pas valide.");
        model.addAttribute("coloralerte", "color : #e74c3c");
        model.addAttribute("color", "color : #e74c3c");
        model.addAttribute("border", "border: 5px solid #e74c3c");
        model.addAttribute("user", new User());
        return "login";
    }
}
