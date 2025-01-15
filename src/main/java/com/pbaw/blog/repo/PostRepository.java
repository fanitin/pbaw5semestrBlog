package com.pbaw.blog.repo;

import com.pbaw.blog.models.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAll(Sort sort);
    List<Post> findByUserId(Long userId);
    List<Post> findByUserId(Long userId, Sort sort);
    List<Post> findByCategoryIdAndIdNot(Long categoryId, Long id);

    @Query("SELECT p FROM Post p LEFT JOIN Like l ON p.id = l.post.id GROUP BY p.id ORDER BY COUNT(l.id) DESC")
    List<Post> findAllPostsSortedByLikes();

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.user.id = :userId")
    long countLikesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.user.id = :userId")
    long countCommentsByUserId(@Param("userId") Long userId);
}