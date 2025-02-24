package com.elharche.security.services;

import com.elharche.security.models.User;
import com.elharche.security.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public void addUser(User user) {
        userRepo.save(user);
    }
}
