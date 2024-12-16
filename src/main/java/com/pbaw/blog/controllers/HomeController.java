package com.pbaw.blog.controllers;

import com.pbaw.blog.models.Post;
import com.pbaw.blog.models.User;
import com.pbaw.blog.repo.PostRepository;
import com.pbaw.blog.repo.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public HomeController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        Iterable<Post> posts = postRepository.findAll(Sort.by(Sort.Order.asc("id")));
        model.addAttribute("posts", posts);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            org.springframework.security.core.userdetails.User springSecurityUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();


            User user = userRepository.findByUsername(springSecurityUser.getUsername()).orElse(null);

            if (user != null) {
                model.addAttribute("message", "Вы залогированы как " + user.getUsername());
            }
        } else {
            model.addAttribute("message", "Не залогирован");
        }
        return "home/home";
    }
}