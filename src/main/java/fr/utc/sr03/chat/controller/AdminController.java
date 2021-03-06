package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.UserService;
import fr.utc.sr03.chat.dao.AttemptsRepository;
import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.Attempts;
import fr.utc.sr03.chat.model.PasswordVerification;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AttemptsRepository attemptsRepository;

    /* Pagination des utilisateurs */
    @GetMapping("{pageNo}")
    public String findPaginated(@PathVariable (value = "pageNo") int pageNo, Model model, WebRequest request) {
        // redirection vers login si aucun utilisateur n'est connecté
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        int pageSize = 5; // nombre d'utilisateurs sur une page
        Page<User> page = userService.findPaginated(pageNo, pageSize); // trouve les bons utilisateurs correspondant à la page associée à pageNo
        List<User> listUsers = page.getContent();
        model.addAttribute("users", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        return "home_admin";
    }

    /* Retourne les utilisateurs désactivés */
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

    /* Ajout d'un nouvel utilisateur */
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

    /* Ajout d'un nouvel utilisateur */
    @PostMapping("add")
    public String addUser(@ModelAttribute User currentUser, @ModelAttribute PasswordVerification verification, Model model, WebRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
                return "redirect:/login";
        }
        if (!userRepository.findByMail(currentUser.getMail()).isEmpty()) { // verification si l'email n'est pas déjà affecté
            model.addAttribute("alerte", "Email déjà affecté.");
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("verification", verification);
            return "add_user";
        }
        if (!currentUser.getMail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { // verification si le mail correspond bien au bon format -@-.-
            model.addAttribute("alerte", "Email non valide");
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("verification", verification);
            return "add_user";
        }
        if (!currentUser.getPassword().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")) { // verification si le mot de passe contient bien un chiffre, une majuscule, une minuscule et au moins 8 caractères
            model.addAttribute("alerte", "Le mot de passe doit contenir un chiffre, une minuscule, une majuscule et au moins 8 caractères");
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("verification", verification);
            return "add_user";
        }
        if (!currentUser.getPassword().equals(verification.getPasswordRepetition())) { // vérification si les deux mots de passes sont identiques
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "Les mots de passe ne sont pas les mêmes.");
            return "add_user";
        }
        // hashage du mot de passe
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(currentUser.getPassword().getBytes("utf8"));
        String hash = String.format("%0128x", new BigInteger(1, digest.digest()));
        currentUser.setPassword(hash);
        currentUser.setAdmin(0);
        currentUser.setActive(1);
        userRepository.save(currentUser);
        Attempts att = new Attempts(); // création d'un nouveau tuple de tentatives de connexion associé à cet utilisateur
        att.setAccount_blocked(0);
        att.setNumber_attempts(0);
        att.setUser(currentUser);
        attemptsRepository.save(att);
        return "redirect:/admin/0";
    }

    /* Suppression de l'utilisateur associé à userId */
    @RequestMapping ("delete/{userId}")
    public String deleteUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        userRepository.deleteUserById(userId);
        return "redirect:/admin/0";
    }

    /* Utilisateur associé à userId devient admin ou n'est plus admin */
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

    /* Utilisateur associé à user Id devient actif ou desactif */
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

    /* Suppression de l'utilisateur desactivé associé à userId */
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

    /* Utilisateur désactivé associé à userId devient admin ou n'est plus admin */
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

    /* Activation d'un utilisateur désactivé associé à userId */
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

    /* Modification de l'utilisateur associé à userId */
    @RequestMapping ("modify/{userId}")
    public String getUser(@PathVariable long userId, Model model, WebRequest request) {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = userRepository.getById(userId);
        Attempts att = attemptsRepository.findFirstByUser(currentUser);
        model.addAttribute("blocked", att.getAccount_blocked());
        model.addAttribute("user", currentUser);
        model.addAttribute("path", userId);
        model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
        model.addAttribute("verification", new PasswordVerification());
        model.addAttribute("alerte", "");
        return "modif_user";
    }

    /* Modification de l'utilisateur associé à userId */
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
        if (!currentUser.getPassword().equals(previousHash)) { // verification que le mot de passe entré correspond bien au sien
            model.addAttribute("user", user);
            model.addAttribute("path", userId);
            model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "L'ancien mot de passe ne correspond pas.");
            return "modif_user";
        }
        if (!userRepository.findByMail(user.getMail()).isEmpty() && userRepository.getByMail(user.getMail()).getId() != userId) { // vérification que l'email n'est pas déjà attribué
            model.addAttribute("alerte", "Email déjà affecté.");
            model.addAttribute("user", user);
            model.addAttribute("verification", verification);
            model.addAttribute("path", userId);
            model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
            return "modif_user";
        }
        if (!user.getMail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { // vérification que le mail correspond bien au format d'un mail
            model.addAttribute("alerte", "Email non valide");
            model.addAttribute("user", user);
            model.addAttribute("verification", verification);
            model.addAttribute("path", userId);
            model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
            return "modif_user";
        }
        if (!verification.getNewPassword().equals(verification.getPasswordRepetition())) { // vérification que les deux nouveaux mots de passes sont identiques (vides ou non)
            model.addAttribute("user", user);
            model.addAttribute("path", userId);
            model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "Les nouveaux mots de passe ne correspondent pas.");
            return "modif_user";
        }
        currentUser.setMail(user.getMail());
        currentUser.setLastName(user.getLastName());
        currentUser.setFirstName(user.getFirstName());
        Attempts att = attemptsRepository.findFirstByUser(currentUser); // déblocage du compte car l'administrateur vient de rentrer le mot de passe
        att.setAccount_blocked(0);
        att.setNumber_attempts(0);
        attemptsRepository.save(att);
        if (verification.getNewPassword() != null && !verification.getNewPassword().isEmpty()) { // vérification si l'utilisateur veut changer son mot de passe
            if (!verification.getNewPassword().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")) {
                model.addAttribute("alerte", "Le mot de passe doit contenir un chiffre, une minuscule, une majuscule et au moins 8 caractères");
                model.addAttribute("user", currentUser);
                model.addAttribute("verification", verification);
                model.addAttribute("path", userId);
                model.addAttribute("title", "Modifier l'utilisateur : " + currentUser.getFirstName() + " " + currentUser.getLastName());
                return "modif_user";
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(verification.getNewPassword().getBytes("utf8"));
            String hash = String.format("%0128x", new BigInteger(1, digest.digest()));
            currentUser.setPassword(hash);
        }
        userRepository.save(currentUser);
        return "redirect:/admin/0";
    }

    /* Modification des informations de l'utilisateur connecté */
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
        model.addAttribute("blocked", 0);
        return "modif_user";
    }

    /* Modification des informations de l'utilisateur connecté */
    @PostMapping("modifySelf")
    public String modifyUserInformation(@ModelAttribute User user, @ModelAttribute PasswordVerification verification, Model model, WebRequest request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (request.getAttribute("connected", WebRequest.SCOPE_SESSION) == null || request.getAttribute("connected", WebRequest.SCOPE_SESSION).equals(false)) {
            return "redirect:/login";
        }
        User currentUser = (User)request.getAttribute("user", WebRequest.SCOPE_SESSION); // récupération de l'utilisateur connecté
        // hashage du mot de passe
        MessageDigest previousDigest = MessageDigest.getInstance("SHA-512");
        previousDigest.reset();
        previousDigest.update(verification.getPreviousPassword().getBytes("utf8"));
        String previousHash = String.format("%0128x", new BigInteger(1, previousDigest.digest()));
        if (!currentUser.getPassword().equals(previousHash)) {
            model.addAttribute("path", "modifySelf");
            model.addAttribute("title", "Modifier mes informations");
            model.addAttribute("user", user);
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "L'ancien mot de passe ne correspond pas.");
            return "modif_user";
        }
        if (!userRepository.findByMail(user.getMail()).isEmpty() && userRepository.getByMail(user.getMail()).getId() != currentUser.getId()) {
            model.addAttribute("alerte", "Email déjà affecté.");
            model.addAttribute("user", user);
            model.addAttribute("verification", verification);
            model.addAttribute("path", "modifySelf");
            model.addAttribute("title", "Modifier mes informations");
            return "modif_user";
        }
        if (!currentUser.getMail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("alerte", "Email non valide");
            model.addAttribute("user", user);
            model.addAttribute("verification", verification);
            model.addAttribute("path", "modifySelf");
            model.addAttribute("title", "Modifier mes informations");
            return "modif_user";
        }
        if (!verification.getNewPassword().equals(verification.getPasswordRepetition())) {
            model.addAttribute("path", "modifySelf");
            model.addAttribute("title", "Modifier mes informations");
            model.addAttribute("user", user);
            model.addAttribute("verification", verification);
            model.addAttribute("alerte", "Les nouveaux mots de passe ne correspondent pas.");
            return "modif_user";
        }
        currentUser.setMail(user.getMail());
        currentUser.setLastName(user.getLastName());
        currentUser.setFirstName(user.getFirstName());
        if (verification.getNewPassword() != null && !verification.getNewPassword().isEmpty()) {
            if (!verification.getNewPassword().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")) {
                model.addAttribute("alerte", "Le mot de passe doit contenir un chiffre, une minuscule, une majuscule et au moins 8 caractères");
                model.addAttribute("user", user);
                model.addAttribute("verification", verification);
                model.addAttribute("path", "modifySelf");
                model.addAttribute("title", "Modifier mes informations");
                return "modif_user";
            }
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
        request.setAttribute("connected", false, WebRequest.SCOPE_SESSION); // l'utilisateur n'est plus connecté
        request.removeAttribute("user", WebRequest.SCOPE_SESSION); // suppression de l'utilisateur dans la session
        return "redirect:/login";
    }
}
