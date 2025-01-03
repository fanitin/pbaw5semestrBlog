package com.pbaw.blog.controllers;

import com.pbaw.blog.models.Post;
import com.pbaw.blog.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LatestController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/latest")
    public String home(Model model) {
        Iterable<Post> posts = postRepository.findAll(Sort.by(Sort.Order.desc("createdAt")));
        model.addAttribute("posts", posts);
        return "latest/latest";
    }
}