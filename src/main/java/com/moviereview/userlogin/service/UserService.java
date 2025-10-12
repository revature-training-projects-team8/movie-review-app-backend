package com.moviereview.userlogin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.moviereview.exception.DuplicateEmailException;
import com.moviereview.exception.DuplicateUserNameException;
import com.moviereview.exception.EmailOrPasswordException;
import com.moviereview.exception.GlobalExceptionHandler;
import com.moviereview.exception.UserNotFoundException;
import com.moviereview.userlogin.model.User;
import com.moviereview.userlogin.repository.UserRepository;

@Service
public class UserService {

    private final GlobalExceptionHandler globalException;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, GlobalExceptionHandler globalException) {
        this.userRepository = userRepository;
        this.globalException = globalException;
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Register user
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail())!= null) {
            throw new DuplicateEmailException("Email already exists");
        }
        else if (userRepository.findByUsername(user.getUsername())!= null) {
            throw new DuplicateUserNameException("Username already exists");
        }
        return userRepository.save(user);
    }
    
    // Login user
    public String loginUser(String username, String password) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return "Login successful";
            }
        }
        throw new EmailOrPasswordException("Invalid username or password");
    }
}
