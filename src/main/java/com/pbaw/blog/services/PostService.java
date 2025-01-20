package com.pbaw.blog.services;

import com.pbaw.blog.models.Comment;
import com.pbaw.blog.models.Post;
import com.pbaw.blog.models.User;
import com.pbaw.blog.repo.CommentRepository;
import com.pbaw.blog.repo.LikeRepository;
import com.pbaw.blog.repo.PostRepository;
import com.pbaw.blog.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, LikeRepository likeRepository, CommentRepository commentRepository,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public List<Post> getAllPosts() {
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

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    public List<Post> getPostsByUserIdSorted(Long userId, Sort sort) {
        return postRepository.findByUserId(userId, sort);
    }

    public int getLikesCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public int getCommentsCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public List<Post> getPosts() {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Order.asc("id")));
        return posts;
    }

    public List<Post> getPostsByCategoryExceptCurrent(Long categoryId, Long postId) {
        List<Post> posts = postRepository.findByCategoryIdAndIdNot(categoryId, postId);
        Collections.shuffle(posts);
        return posts.stream()
                .limit(3)
                .collect(Collectors.toList());
    }

    public int getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public int getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isLikedByUser(int postId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }
        return likeRepository.existsByPostIdAndUserId(postId, user.getId());
    }

    public List<Comment> getCommentsForPost(Long postId) {
        return commentRepository.findByPostId(postId); // Получаем все комментарии для поста
    }

    public Page<Post> getPostsSortedByLikes(Pageable pageable) {
        return postRepository.findPostsSortedByLikes(pageable);
    }

    public Page<Post> getPostsByCategoryWithPagination(int categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByCategoryId((long) categoryId, pageable);
    }

    public Page<Post> getLatestPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return postRepository.findAll(pageable);
    }
    public Page<Post> getPostsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return postRepository.findByUserId(userId, pageable);
    }
}