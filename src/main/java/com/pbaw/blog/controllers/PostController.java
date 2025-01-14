package com.pbaw.blog.controllers;

import com.pbaw.blog.models.*;
import com.pbaw.blog.repo.LikeRepository;
import com.pbaw.blog.repo.PostRepository;
import com.pbaw.blog.repo.UserRepository;
import com.pbaw.blog.services.CategoryService;
import com.pbaw.blog.services.CustomUserDetailsService;
import com.pbaw.blog.services.LikeService;
import com.pbaw.blog.services.PostService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class PostController {
    @Value("${upload.dir}")
    private String uploadDir;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final CategoryService categoryService;
    private final LikeRepository likeRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final LikeService likeService;

    public PostController(PostRepository postRepository, UserRepository userRepository, PostService postService,
                          CategoryService categoryService, LikeRepository likeRepository,
                          CustomUserDetailsService customUserDetailsService, LikeService likeService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postService = postService;
        this.categoryService = categoryService;
        this.likeRepository = likeRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.likeService = likeService;
    }

    @RequestMapping("/post/{id}")
    public String getPost(Model model, @PathVariable int id, Authentication authentication) {
        Post post = postService.getPostById(id);
        int commentCount = postService.getCommentCount(post.getId());
        int likeCount = postService.getLikeCount(post.getId());
        boolean isLikedByUser = postService.isLikedByUser(id, authentication);

        LocalDateTime createdAt = post.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm");
        String formattedDate = createdAt.format(formatter);

        List<Post> relatedPosts = postService.getPostsByCategoryExceptCurrent(post.getCategory().getId(), post.getId());
        List<Comment> comments = postService.getCommentsForPost(post.getId());

        model.addAttribute("post", post);
        model.addAttribute("date", formattedDate);
        model.addAttribute("relatedPosts", relatedPosts);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("isLikedByUser", isLikedByUser);
        model.addAttribute("comments", comments);

        return "post/post";
    }


    @GetMapping("/post/add")
    public String addPost(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "post/add";
    }

    @PostMapping("/post/add")
    public String addPostSubmit(Model model, @RequestParam("title") String title,
                                @RequestParam("content") String content, @RequestParam("image")
                                    MultipartFile image, @RequestParam("categoryId") int categoryId){
        LocalDateTime createdAt = LocalDateTime.now();
        String imagePath = saveImage(image);

        if (imagePath == null) {
            model.addAttribute("error", "Invalid image file.");
            return "error"; // Страница с ошибкой
        }

        Category category = categoryService.findById(categoryId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User springSecurityUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User user = userRepository.findByUsername(springSecurityUser.getUsername()).orElse(null);


        Post post = new Post(title, content, category, imagePath, createdAt, user);
        postRepository.save(post);
        return "redirect:/latest";
    }

    @PostMapping("/post/like/{postId}")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Unauthorized"));
            }

            String username = authentication.getName();
            Long userId = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username))
                    .getId();

            String success = likeService.toggleLike(userId, postId);
            int likeCount = postService.getLikeCount(postId);

            return ResponseEntity.ok(Map.of("success", success, "likeCount", likeCount));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }


    private String saveImage(MultipartFile image) {
        if (image.isEmpty()) {
            return null;
        }

        try {
            if (!isValidImage(image)) {
                return null;
            }

            String uniqueFileName = UUID.randomUUID().toString();

            Path path = Path.of(uploadDir, uniqueFileName);
            Files.createDirectories(path.getParent());
            String normalizedPath = path.toString().replace("\\", "/");
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }

            return normalizedPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isValidImage(MultipartFile file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            return bufferedImage != null;
        } catch (IOException e) {
            return false;
        }
    }
}
