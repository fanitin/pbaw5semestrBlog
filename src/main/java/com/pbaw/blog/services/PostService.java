package com.pbaw.blog.services;

import com.pbaw.blog.models.Post;
import com.pbaw.blog.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post getPostById(int id) {
        return postRepository.findById(id).orElse(null);
    }

    public void deletePostById(int id) {
        postRepository.deleteById(id);
    }
}
