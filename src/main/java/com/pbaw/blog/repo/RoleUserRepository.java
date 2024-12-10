package com.pbaw.blog.repo;

import com.pbaw.blog.models.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleUserRepository extends JpaRepository<RoleUser, Integer> {
}
