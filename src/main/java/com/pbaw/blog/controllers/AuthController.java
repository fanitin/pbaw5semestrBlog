package com.pbaw.blog.controllers;

import com.pbaw.blog.models.Role;
import com.pbaw.blog.models.RoleUser;
import com.pbaw.blog.models.User;
import com.pbaw.blog.repo.RoleRepository;
import com.pbaw.blog.repo.RoleUserRepository;
import com.pbaw.blog.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleUserRepository roleUserRepository;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, RoleUserRepository roleUserRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleUserRepository = roleUserRepository;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @RequestParam("username") String username, @RequestParam("email") String email,
                               @RequestParam("password") String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email is already taken.");
            return "auth/register";
        }
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username is already taken.");
            return "auth/register";
        }
        LocalDateTime createdAt = LocalDateTime.now();
        User user = new User(username, email, password, createdAt);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);

        Role userRole = roleRepository.findByName("User")
                .orElseThrow(() -> new RuntimeException("Nie ma takiej roli"));

        RoleUser roleUser = new RoleUser();
        roleUser.setUser(user);
        roleUser.setRole(userRole);
        roleUserRepository.save(roleUser);

        return "redirect:/latest";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginUser(Model model, @RequestParam("username") String username, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        System.out.println("Username from form: " + username);
        User user = userRepository.findByUsername(username).orElse(null);
        System.out.println(user);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("message", "Welcome, " + user.getUsername());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logoutUser(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully.");
        return "redirect:/";
    }
}