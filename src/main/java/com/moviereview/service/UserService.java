package com.moviereview.service;

import com.moviereview.model.User;
import com.moviereview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> update(Long id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    user.setId(id);
                    return userRepository.save(user);
                });
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
