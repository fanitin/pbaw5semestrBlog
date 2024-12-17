package com.pbaw.blog.controllers;

import com.pbaw.blog.models.Post;
import com.pbaw.blog.repo.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Controller
public class PostController {
    private final PostRepository postRepository;
    @Value("${upload.dir}")
    private String uploadDir;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/post/add")
    public String addPost(Model model) {
        return "addPost/addPost";
    }

    @PostMapping("/post/add")
    public String addPostSubmit(Model model, @RequestParam("title") String title,
                                @RequestParam("content") String content, @RequestParam("image")
                                    MultipartFile image){
        LocalDateTime createdAt = LocalDateTime.now();
        String imagePath = saveImage(image);

        if (imagePath == null) {
            model.addAttribute("error", "Invalid image file.");
            return "error"; // Страница с ошибкой
        }

        Post post = new Post(title, content, null, imagePath, createdAt, null);
        postRepository.save(post);
        return "redirect:/latest";
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

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }

            return path.toString();
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
