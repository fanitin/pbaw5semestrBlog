package com.pbaw.blog.controllers;

import com.pbaw.blog.models.Post;
import com.pbaw.blog.repo.PostRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final PostRepository postRepository;

    public HomeController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home");
        Iterable<Post> posts = postRepository.findAll(Sort.by(Sort.Order.asc("id")));
        model.addAttribute("posts", posts);
        return "home/home";
    }
}