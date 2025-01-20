package com.pbaw.blog.controllers;

import com.pbaw.blog.models.Post;
import com.pbaw.blog.repo.PostRepository;
import com.pbaw.blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LatestController {
    private final PostRepository postRepository;
    private final PostService postService;

    public LatestController(PostRepository postRepository, PostService postService) {
        this.postRepository = postRepository;
        this.postService = postService;
    }

    @GetMapping("/latest")
    public String latestPosts(@RequestParam(defaultValue = "1") int page,
                              Model model) {
        int pageSize = 9; // Количество постов на одной странице
        Page<Post> posts = postService.getLatestPosts(page - 1, pageSize);

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());
        return "latest/latest";
    }
}