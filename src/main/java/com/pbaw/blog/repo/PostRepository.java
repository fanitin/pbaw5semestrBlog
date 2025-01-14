package com.pbaw.blog.repo;

import com.pbaw.blog.models.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAll(Sort sort);
    List<Post> findByUserId(Long userId);
    List<Post> findByUserId(Long userId, Sort sort);
    List<Post> findByCategoryIdAndIdNot(Long categoryId, Long id);
}
