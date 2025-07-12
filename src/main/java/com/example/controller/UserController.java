// com.skillswap.controller.UserController.java
package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.UserProfileDto;
import com.example.dto.SearchDto;
import com.example.service.UserService;
import com.example.service.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileDto profile = userService.getUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestPart("profile") UserProfileDto profileDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            if (photo != null && !photo.isEmpty()) {
                String photoUrl = saveProfilePhoto(photo);
                profileDto.setProfilePhoto(photoUrl);
            }
            userService.validateSkills(profileDto.getOfferedSkills(), profileDto.getWantedSkills());
            userService.updateProfile(userPrincipal.getId(), profileDto);
            return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Failed to upload photo"));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<UserProfileDto>> getPublicUsers() {
        return ResponseEntity.ok(userService.getAllPublicUsers());
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserProfileDto>> searchUsers(@Valid @RequestBody SearchDto searchDto) {
        return ResponseEntity.ok(userService.searchUsers(searchDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable String id) {
        userService.incrementProfileViews(id);
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    private String saveProfilePhoto(MultipartFile photo) throws IOException {
        String uploadDir = "uploads/profile-photos/";
        String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, photo.getBytes());
        return "/uploads/profile-photos/" + fileName;
    }
}