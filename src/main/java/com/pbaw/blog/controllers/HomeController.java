package com.pbaw.blog.controllers;

import com.pbaw.blog.models.Post;
import com.pbaw.blog.models.User;
import com.pbaw.blog.repo.PostRepository;
import com.pbaw.blog.repo.UserRepository;
import com.pbaw.blog.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public HomeController(PostRepository postRepository, UserRepository userRepository, PostService postService) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "9") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.getPostsSortedByLikes(pageable);

        model.addAttribute("posts", postPage.getContent()); // Посты текущей страницы
        model.addAttribute("currentPage", page);           // Текущая страница
        model.addAttribute("totalPages", postPage.getTotalPages()); // Общее количество страниц

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            org.springframework.security.core.userdetails.User springSecurityUser =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

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