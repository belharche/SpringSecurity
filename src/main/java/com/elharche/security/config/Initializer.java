package com.elharche.security.config;

import com.elharche.security.models.Role;
import com.elharche.security.models.User;
import com.elharche.security.repos.RoleRepo;
import com.elharche.security.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class Initializer {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner init() {

        return args -> {
            if (roleRepo.count() == 0) {
                Role UserRole = Role.builder()
                        .name("ROLE_USER")
                        .build();
                Role AdminRole = Role.builder()
                        .name("ROLE_ADMIN")
                        .build();
                roleRepo.save(UserRole);
                roleRepo.save(AdminRole);
            }

            if (userRepo.count() == 0) {
                Role adminRole = roleRepo.findByName("ROLE_ADMIN");
                Role userRole = roleRepo.findByName("ROLE_USER");
                User admin = User.builder()
                        .email("admin@elharche.com")
                        .name("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(List.of(adminRole, userRole))
                        .build();
                User user = User.builder()
                        .email("user@elharche.com")
                        .name("user")
                        .password(passwordEncoder.encode("user"))
                        .roles(List.of(userRole))
                        .build();
                userRepo.save(admin);
                userRepo.save(user);
            }
        };
    }

}
