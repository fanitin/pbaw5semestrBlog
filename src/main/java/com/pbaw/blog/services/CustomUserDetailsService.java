package com.pbaw.blog.services;

import com.pbaw.blog.models.User;
import com.pbaw.blog.repo.RoleUserRepository;
import com.pbaw.blog.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleUserRepository roleUserRepository;

    public CustomUserDetailsService(UserRepository userRepository, RoleUserRepository roleUserRepository) {
        this.userRepository = userRepository;
        this.roleUserRepository = roleUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));

        List<SimpleGrantedAuthority> authorities = roleUserRepository.findByUser(user)
                .stream()
                .map(roleUser -> new SimpleGrantedAuthority(roleUser.getRole().getName()))
                .toList();
        System.out.println("Authorities: " + authorities);

        return new org.springframework.security.core.userdetails
                .User(user.getUsername(), user.getPassword(), authorities);
    }
}
