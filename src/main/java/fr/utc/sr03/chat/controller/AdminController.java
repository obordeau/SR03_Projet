package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.PasswordVerification;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.SyncFailedException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String getUserList(Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("title", "Tous les utilisateurs");
        model.addAttribute("path", 0);
        return "home_admin";
    }

    @GetMapping("desactivated")
    public String getDesactivatedUsers(Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        List<User> users = userRepository.findUsersByActiveIs(0);
        model.addAttribute("users", users);
        model.addAttribute("title", "Utilisateurs désactivés");
        model.addAttribute("path", 1);
        return "home_admin";
    }

    @GetMapping("add")
    public String getUserForm(Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", new User());
        model.addAttribute("verification", new PasswordVerification());
        return "add_user";
    }

    @PostMapping("add")
    public String addUser(@ModelAttribute User currentUser, @ModelAttribute PasswordVerification verification, Model model, WebRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
                return "redirect:/login";
        }
        if (!userRepository.findByMail(currentUser.getMail()).isEmpty()) {
            System.out.println("Email déja affecte.");
            model.addAttribute("currentUser", new User());
            model.addAttribute("path", "add");
            model.addAttribute("title", "Ajouter un utilisateur");
            model.addAttribute("verification", new PasswordVerification());
            return "add_user";
        }
        if (!currentUser.getPassword().equals(verification.getPasswordRepetition())) {
            System.out.println("Les mots de passe ne sont pas les mêmes.");
            model.addAttribute("currentUser", new User());
            model.addAttribute("path", "add");
            model.addAttribute("title", "Ajouter un utilisateur");
            model.addAttribute("verification", new PasswordVerification());
            return "add_user";
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(currentUser.getPassword().getBytes("utf8"));
        String hash = String.format("%0128x", new BigInteger(1, digest.digest()));
        currentUser.setPassword(hash);
        currentUser.setAdmin(0);
        currentUser.setActive(1);
        userRepository.save(currentUser);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("title", "Tous les utilisateurs");
        return "home_admin";
    }

    @RequestMapping ("delete/{userId}")
    public String deleteUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        userRepository.deleteUserById(userId);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("title", "Tous les utilisateurs");
        model.addAttribute("path", 0);
        return "home_admin";
    }

    @RequestMapping ("admin/{userId}")
    public String adminUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = userRepository.getById(userId);
        if (currentUser.isAdmin() == 1) {
            currentUser.setAdmin(0);
        } else {
            currentUser.setAdmin(1);
        }
        userRepository.save(currentUser);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("title", "Tous les utilisateurs");
        model.addAttribute("path", 0);
        return "home_admin";
    }

    @RequestMapping ("active/{userId}")
    public String activeUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = userRepository.getById(userId);
        if (currentUser.isActive() == 1) {
            currentUser.setActive(0);
        } else {
            currentUser.setActive(1);
        }
        userRepository.save(currentUser);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("title", "Tous les utilisateurs");
        model.addAttribute("path", 0);
        return "home_admin";
    }

    @RequestMapping ("desactivated/delete/{userId}")
    public String deleteDesactivatedUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        userRepository.deleteUserById(userId);
        List<User> users = userRepository.findUsersByActiveIs(0);
        model.addAttribute("users", users);
        model.addAttribute("title", "Utilisateurs désactivés");
        model.addAttribute("path", 1);
        return "home_admin";
    }

    @RequestMapping ("desactivated/admin/{userId}")
    public String adminDesactivatedUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = userRepository.getById(userId);
        if (currentUser.isAdmin() == 1) {
            currentUser.setAdmin(0);
        } else {
            currentUser.setAdmin(1);
        }
        userRepository.save(currentUser);
        List<User> users = userRepository.findUsersByActiveIs(0);
        model.addAttribute("users", users);
        model.addAttribute("title", "Utilisateurs désactivés");
        model.addAttribute("path", 1);
        return "home_admin";
    }

    @RequestMapping ("desactivated/active/{userId}")
    public String activeDesactivatedUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = userRepository.getById(userId);
        if (currentUser.isActive() == 1) {
            currentUser.setActive(0);
        } else {
            currentUser.setActive(1);
        }
        userRepository.save(currentUser);
        List<User> users = userRepository.findUsersByActiveIs(0);
        model.addAttribute("users", users);
        model.addAttribute("title", "Utilisateurs désactivés");
        model.addAttribute("path", 1);
        return "home_admin";
    }

    @RequestMapping ("modify/{userId}")
    public String getUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = userRepository.getById(userId);
        model.addAttribute("user", currentUser);
        model.addAttribute("path", userId);
        model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
        model.addAttribute("verification", new PasswordVerification());
        return "modif_user";
    }

    @PostMapping ("modify/{userId}")
    public String modifyUser(@PathVariable long userId, @ModelAttribute User user, @ModelAttribute PasswordVerification verification, Model model, WebRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = userRepository.getById(userId);
        MessageDigest previousDigest = MessageDigest.getInstance("SHA-512");
        previousDigest.reset();
        previousDigest.update(verification.getPreviousPassword().getBytes("utf8"));
        String previousHash = String.format("%0128x", new BigInteger(1, previousDigest.digest()));
        if (!currentUser.getPassword().equals(previousHash)) {
            System.out.println("L'ancien mot de passe ne correspond pas");
            model.addAttribute("user", currentUser);
            model.addAttribute("path", userId);
            model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
            model.addAttribute("verification", new PasswordVerification());
            return "modif_user";
        }
        if (!verification.getNewPassword().equals(verification.getPasswordRepetition())) {
            System.out.println("Les nouveaux mots de passe ne correspondent pas");
            model.addAttribute("user", currentUser);
            model.addAttribute("path", userId);
            model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
            model.addAttribute("verification", new PasswordVerification());
            return "modif_user";
        }
        currentUser.setMail(user.getMail());
        currentUser.setLastName(user.getLastName());
        currentUser.setFirstName(user.getFirstName());
        if (verification.getNewPassword() != null && !verification.getNewPassword().isEmpty()) {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(verification.getNewPassword().getBytes("utf8"));
            String hash = String.format("%0128x", new BigInteger(1, digest.digest()));
            currentUser.setPassword(hash);
        }
        userRepository.save(currentUser);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("title", "Tous les utilisateurs");
        return "home_admin";
    }
    @GetMapping("modifySelf")
    public String getUserInformation(Model model, WebRequest request)
    {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        model.addAttribute("path", "modifySelf");
        model.addAttribute("title", "Modifier mes informations");
        model.addAttribute("user", (User)request.getAttribute("user", WebRequest.SCOPE_SESSION));
        model.addAttribute("verification", new PasswordVerification());
        return "modif_user";
    }

    @PostMapping("modifySelf")
    public String modifyUserInformation(@ModelAttribute User user, @ModelAttribute PasswordVerification verification, Model model, WebRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = (User)request.getAttribute("user", WebRequest.SCOPE_SESSION);
        MessageDigest previousDigest = MessageDigest.getInstance("SHA-512");
        previousDigest.reset();
        previousDigest.update(verification.getPreviousPassword().getBytes("utf8"));
        String previousHash = String.format("%0128x", new BigInteger(1, previousDigest.digest()));
        if (!currentUser.getPassword().equals(previousHash)) {
            System.out.println("L'ancien mot de passe ne correspond pas");
            model.addAttribute("path", "modifySelf");
            model.addAttribute("title", "Modifier mes informations");
            model.addAttribute("user", currentUser);
            model.addAttribute("verification", new PasswordVerification());
            return "modif_user";
        }
        if (!verification.getNewPassword().equals(verification.getPasswordRepetition())) {
            System.out.println("Les nouveaux mots de passe ne correspondent pas");
            model.addAttribute("path", "modifySelf");
            model.addAttribute("title", "Modifier mes informations");
            model.addAttribute("user", currentUser);
            model.addAttribute("verification", new PasswordVerification());
            return "modif_user";
        }
        currentUser.setMail(user.getMail());
        currentUser.setLastName(user.getLastName());
        currentUser.setFirstName(user.getFirstName());
        if (verification.getNewPassword() != null && !verification.getNewPassword().isEmpty()) {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(verification.getNewPassword().getBytes("utf8"));
            String hash = String.format("%0128x", new BigInteger(1, digest.digest()));
            currentUser.setPassword(hash);
        }
        request.setAttribute("user", currentUser, WebRequest.SCOPE_SESSION);
        userRepository.save(currentUser);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("title", "Tous les utilisateurs");
        return "home_admin";
    }

    @GetMapping("deconnect")
    public String deconnexion(WebRequest request)
    {
        request.setAttribute("connected", false, WebRequest.SCOPE_SESSION);
        request.removeAttribute("user", WebRequest.SCOPE_SESSION);
        return "redirect:/login";
    }
}
