package com.example.controller;


import com.example.dto.UserResponse;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestParam String username) {
        try {
            UserResponse user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all active users
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getAllActiveUsers() {
        List<UserResponse> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get all users
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Deactivate user
     */
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable UUID userId) {
        try {
            UserResponse deactivatedUser = userService.deactivateUser(userId);
            return ResponseEntity.ok(deactivatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Activate user
     */
    @PutMapping("/{userId}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable UUID userId) {
        try {
            UserResponse activatedUser = userService.activateUser(userId);
            return ResponseEntity.ok(activatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}