package com.user.userManagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.user.userManagement.model.User;
import com.user.userManagement.service.UserDetailsServiceImpl;

@RestController
public class UserController {

    private final UserDetailsServiceImpl userService;

    public UserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    // ------------ Admin endpoints start-------------//
    @PostMapping("/admin/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        // Implement user registration logic for admin
        boolean success = userService.registerUser(newUser);
        if (success) {
            return ResponseEntity.ok("User registered successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to register user");
        }
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        // Check if the current user has admin authority
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            List<User> users = userService.getAllUsers(page, size);
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @PatchMapping("/admin/users/{userId}")
    public ResponseEntity<String> updateUserDetails(@PathVariable UUID userId,
            @RequestBody User updatedUser) {
        // Implement logic to update user details by admin
        boolean success = userService.updateUserDetails(userId, updatedUser);
        if (success) {
            return ResponseEntity.ok("User details updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ---------------Users endpoints start------------//
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // Implement logic to retrieve user profile
        User user = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/profile/{userId}") // Assuming userId is passed as a path variable
    public ResponseEntity<String> updateUserProfile(@PathVariable UUID userId,
            @RequestBody User updatedUser) {
        // Implement logic to update user profile
        boolean success = userService.updateUserProfile(userId, updatedUser);
        if (success) {
            return ResponseEntity.ok("User profile updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to update user profile");
        }
    }

}