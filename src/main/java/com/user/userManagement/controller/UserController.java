package com.user.userManagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.user.userManagement.model.User;
import com.user.userManagement.service.UserDetailsServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    private final UserDetailsServiceImpl userService;

    public UserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    // User endpoints
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/profile/{userId}")
    public ResponseEntity<String> patchUserProfile(@PathVariable UUID userId, @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the current user is updating their own profile
        if (userId.equals(((UserDetailsServiceImpl) userDetails).getId())) {
            if (userService.patchUserProfile(userId, updates)) {
                return ResponseEntity.ok("User profile updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own profile");
    }

    // Admin endpoints
    @PostMapping("/admin/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the current user has admin authority
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            userService.registerUser(newUser);
            return ResponseEntity.ok("User registered successfully");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can register users");
    }

    @PatchMapping("/admin/users/{userId}")
    public ResponseEntity<String> patchUser(@PathVariable UUID userId, @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the current user has admin authority
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            if (userService.patchUser(userId, updates)) {
                return ResponseEntity.ok("User updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can edit users");
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        // Check if the current user has admin authority
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
}