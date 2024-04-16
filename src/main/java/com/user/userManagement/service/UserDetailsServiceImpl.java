package com.user.userManagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.userManagement.model.User;
import com.user.userManagement.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository repository, @Lazy PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Method for Admin to register user
    public boolean registerUser(User newUser) {
        if (repository.findByUsername(newUser.getUsername()).isPresent()) {
            return false; // User already exists
        }

        // Encode password before saving
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        repository.save(newUser);
        return true;
    }

    // Method for user to update profile info
    public boolean updateUserProfile(UUID userId, User updatedUser) {
        Optional<User> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Update user profile fields
            user.setEmail(updatedUser.getEmail());
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setUsername(updatedUser.getUsername());

            repository.save(user);
            return true;
        }
        return false;
    }

    // Method for Admin to update user profile
    public boolean updateUserDetails(UUID userId, User updatedUser) {
        Optional<User> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Update user details
            user.setEmail(updatedUser.getEmail());
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setUsername(updatedUser.getUsername());
            user.setPassword(updatedUser.getPassword());

            repository.save(user);
            return true;
        }
        return false;
    }

    // Method for User to view profile
    public User getUserProfile(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Method for Admin to view all available users
    public List<User> getAllUsers(int limit, int offset) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<User> userPage = repository.findAll(pageable);
        return userPage.getContent();
    }

}
