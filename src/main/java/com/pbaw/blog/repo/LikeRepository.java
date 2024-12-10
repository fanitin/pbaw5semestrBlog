package com.pbaw.blog.repo;

import com.pbaw.blog.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {
}
