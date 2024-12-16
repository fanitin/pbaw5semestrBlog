package com.pbaw.blog.repo;

import com.pbaw.blog.models.RoleUser;
import com.pbaw.blog.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleUserRepository extends JpaRepository<RoleUser, Long> {
    List<RoleUser> findByUser(User user);
}
