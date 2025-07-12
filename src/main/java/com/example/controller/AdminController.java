// com.skillswap.controller.AdminController.java
package com.example.controller;

import com.example.dto.AdminMessageDto;
import com.example.dto.AdminDashboardStats;
import com.example.dto.ApiResponse;
import com.example.model.AdminMessage;
import com.example.model.SwapRequest;
import com.example.model.User;
import com.example.service.AdminService;
import com.example.service.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/swaps")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SwapRequest>> getAllSwapRequests() {
        return ResponseEntity.ok(adminService.getAllSwapRequests());
    }

    @PutMapping("/users/{userId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> banUser(@PathVariable String userId) {
        return ResponseEntity.ok(adminService.banUser(userId));
    }

    @PutMapping("/users/{userId}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> unbanUser(@PathVariable String userId) {
        return ResponseEntity.ok(adminService.unbanUser(userId));
    }

    @PostMapping("/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminMessage> sendPlatformMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AdminMessageDto messageDto) {
        return ResponseEntity.ok(adminService.sendPlatformMessage(userPrincipal.getId(), messageDto));
    }

    @GetMapping("/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminMessage>> getAllPlatformMessages() {
        return ResponseEntity.ok(adminService.getAllPlatformMessages());
    }

    @DeleteMapping("/swaps/{swapId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteSwapRequest(@PathVariable String swapId) {
        adminService.deleteSwapRequest(swapId);
        return ResponseEntity.ok(new ApiResponse(true, "Swap request deleted successfully"));
    }

    @PutMapping("/users/{userId}/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> makeUserAdmin(@PathVariable String userId) {
        return ResponseEntity.ok(adminService.makeUserAdmin(userId));
    }

    @PutMapping("/users/{userId}/remove-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> removeAdminRole(@PathVariable String userId) {
        return ResponseEntity.ok(adminService.removeAdminRole(userId));
    }

    @DeleteMapping("/users/{userId}/skills/{skillName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> rejectSkill(
            @PathVariable String userId,
            @PathVariable String skillName) {
        adminService.rejectSkillDescription(userId, skillName);
        return ResponseEntity.ok(new ApiResponse(true, "Skill rejected successfully"));
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardStats> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}