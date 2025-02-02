package com.pbaw.blog.repo;

import com.pbaw.blog.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    int countByPostId(Long postId);
    List<Comment> findByPostId(Long postId);
}
