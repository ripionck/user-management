package com.user.userManagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user.userManagement.model.User;
import com.user.userManagement.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Method for user registration
    public void registerUser(User newUser) {
        repository.save(newUser);
    }

    // Method for updating user profile
    public void updateUserProfile(String username, User updatedUser) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update user profile fields
        user.setEmail(updatedUser.getEmail());
        // Update other profile fields as needed

        repository.save(user);
    }

    // Method for fetching user profile
    public User getUserProfile(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Method for fetching all users (for admin)
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // Method for editing a user (for admin)
    public void editUser(UUID userId, User updatedUser) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update user fields based on the updatedUser
        user.setEmail(updatedUser.getEmail());
        // Update other fields as needed

        repository.save(user);
    }
}
