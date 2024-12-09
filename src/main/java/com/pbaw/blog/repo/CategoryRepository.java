package com.pbaw.blog.repo;

import com.pbaw.blog.models.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
