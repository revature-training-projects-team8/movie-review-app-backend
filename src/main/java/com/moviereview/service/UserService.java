package com.moviereview.service;

import com.moviereview.exception.DuplicateResourceException;
import com.moviereview.model.User;
import com.moviereview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // For simplicity, storing plain text (not recommended for production!)
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean validatePassword(User user, String password) {
        return user.getPassword().equals(password); // Simple password validation
    }
}