package com.example.service;
import com.example.dto.AuthResponse;
import com.example.dto.LoginRequest;
import com.example.dto.UserRegistrationRequest;
import com.example.dto.UserResponse;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.util.JwtUtil;
import com.example.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user
     */
    public AuthResponse registerUser(UserRegistrationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Hash password using SHA256
        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());

        // Create user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                hashedPassword
        );

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getUsername());

        return new AuthResponse(token, savedUser.getUser_id(), savedUser.getUsername(),
                savedUser.getEmail());
    }

    /**
     * Authenticate user and return JWT token
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return new AuthResponse(token, user.getUser_id(), user.getUsername(),
                user.getEmail());
    }

    /**
     * Get user by ID
     */
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToUserResponse(user);
    }

    /**
     * Get user by username
     */
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToUserResponse(user);
    }

    /**
     * Get all users
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active users
     */
    public List<UserResponse> getAllActiveUsers() {
        return userRepository.findAllActiveUsers().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deactivate user
     */
    public UserResponse deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIs_active(false);
        User updatedUser = userRepository.save(user);

        return convertToUserResponse(updatedUser);
    }

    /**
     * Activate user
     */
    public UserResponse activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIs_active(true);
        User updatedUser = userRepository.save(user);

        return convertToUserResponse(updatedUser);
    }

    /**
     * Convert User entity to UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getUser_id(),
                user.getUsername(),
                user.getEmail(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.isIs_active()
        );
    }
}