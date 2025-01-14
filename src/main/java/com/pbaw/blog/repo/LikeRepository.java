package com.pbaw.blog.repo;

import com.pbaw.blog.models.Like;
import com.pbaw.blog.models.Post;
import com.pbaw.blog.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    int countByPostId(Long postId);
    boolean existsByPostIdAndUserId(int postId, Long userId);
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
}