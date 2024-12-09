package com.pbaw.blog.repo;

import com.pbaw.blog.models.Like;
import org.springframework.data.repository.CrudRepository;

public interface LikeRepository extends CrudRepository<Like, Long> {
}
