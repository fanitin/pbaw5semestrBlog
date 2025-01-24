package com.pbaw.blog.repo;

import com.pbaw.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Post> findByUserId(Long userId, Pageable pageable);
    List<Post> findByUserId(Long userId, Sort sort);
    List<Post> findByCategoryIdAndIdNot(Long categoryId, Long id);

    @Query("SELECT p FROM Post p LEFT JOIN p.likes l GROUP BY p.id ORDER BY COUNT(l.id) DESC")
    Page<Post> findPostsSortedByLikes(Pageable pageable);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.user.id = :userId")
    long countLikesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.user.id = :userId")
    long countCommentsByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId")
    Page<Post> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    Page<Post> findByTitleContaining(String title, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.category.name LIKE %:categoryName%")
    Page<Post> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.username LIKE %:username%")
    Page<Post> findByUserUsername(@Param("username") String username, Pageable pageable);
}