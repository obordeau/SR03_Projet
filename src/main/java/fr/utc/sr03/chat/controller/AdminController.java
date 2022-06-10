package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.UserService;
import fr.utc.sr03.chat.dao.UserPaging;
import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.PasswordVerification;
import fr.utc.sr03.chat.model.User;
import jdk.jfr.Frequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @Autowired
    private UserService userService;

    @GetMapping("{pageNo}")
    public String findPaginated(@PathVariable (value = "pageNo") int pageNo, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        int pageSize = 5;
        Page<User> page = userService.findPaginated(pageNo, pageSize);
        List<User> listUsers = page.getContent();
        model.addAttribute("users", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        return "home_admin";
    }
/*
    @GetMapping("")
    public String getUserList(Model model, WebRequest request) {
        return "redirect:/admin/page/0";
    }
*/

    @GetMapping("desactivated")
    public String getDesactivatedUsers(Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        List<User> users = userRepository.findUsersByActiveIs(0);
        if (users.isEmpty()) {
            model.addAttribute("empty", 1);
        } else {
            model.addAttribute("empty", 0);
            model.addAttribute("users", users);
        }
        return "home_desactivated";
    }

    @GetMapping("add")
    public String getUserForm(Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", new User());
        model.addAttribute("verification", new PasswordVerification());
        model.addAttribute("alerte", "");
        return "add_user";
    }

    @PostMapping("add")
    public String addUser(@ModelAttribute User currentUser, @ModelAttribute PasswordVerification verification, Model model, WebRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
                return "redirect:/login";
        }
        if (!userRepository.findByMail(currentUser.getMail()).isEmpty()) {
            model.addAttribute("alerte", "Email déjà affecté.");
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("verification", verification);
            return "add_user";
        }
        if (!currentUser.getPassword().equals(verification.getPasswordRepetition())) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "Les mots de passe ne sont pas les mêmes.");
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
        return "redirect:/admin/0";
    }

    @RequestMapping ("delete/{userId}")
    public String deleteUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        userRepository.deleteUserById(userId);
        return "redirect:/admin/0";
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
        return "redirect:/admin/0";
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
        return "redirect:/admin/0";
    }

    @RequestMapping ("desactivated/delete/{userId}")
    public String deleteDesactivatedUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        userRepository.deleteUserById(userId);
        List<User> users = userRepository.findUsersByActiveIs(0);
        if (users.isEmpty()) {
            model.addAttribute("empty", 1);
        } else {
            model.addAttribute("users", users);
            model.addAttribute("empty", 0);
        }
        return "home_desactivated";
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
        if (users.isEmpty()) {
            model.addAttribute("empty", 1);
        } else {
            model.addAttribute("users", users);
            model.addAttribute("empty", 0);
        }
        return "home_desactivated";
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
        if (users.isEmpty()) {
            model.addAttribute("empty", 1);
        } else {
            model.addAttribute("users", users);
            model.addAttribute("empty", 0);
        }
        return "home_desactivated";
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
        model.addAttribute("alerte", "");
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
            model.addAttribute("user", currentUser);
            model.addAttribute("path", userId);
            model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "L'ancien mot de passe ne correspond pas.");
            return "modif_user";
        }
        if (!verification.getNewPassword().equals(verification.getPasswordRepetition())) {
            model.addAttribute("user", currentUser);
            model.addAttribute("path", userId);
            model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "Les nouveaux mots de passe ne correspondent pas.");
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
        return "redirect:/admin/0";
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
        model.addAttribute("alerte", "");
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
            model.addAttribute("path", "modifySelf");
            model.addAttribute("title", "Modifier mes informations");
            model.addAttribute("user", currentUser);
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "L'ancien mot de passe ne correspond pas.");
            return "modif_user";
        }
        if (!verification.getNewPassword().equals(verification.getPasswordRepetition())) {
            model.addAttribute("path", "modifySelf");
            model.addAttribute("title", "Modifier mes informations");
            model.addAttribute("user", currentUser);
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "Les nouveaux mots de passe ne correspondent pas.");
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
        return "redirect:/admin/0";
    }

    @GetMapping("deconnect")
    public String deconnexion(WebRequest request, Model model)
    {
        request.setAttribute("connected", false, WebRequest.SCOPE_SESSION);
        request.removeAttribute("user", WebRequest.SCOPE_SESSION);
        return "redirect:/login";
    }
}
