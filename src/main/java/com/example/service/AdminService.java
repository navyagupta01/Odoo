// com.example.service.AdminService.java
package com.example.service;

import com.example.dto.AdminMessageDto;
import com.example.dto.AdminDashboardStats;
import com.example.model.AdminMessage;
import com.example.model.SwapRequest;
import com.example.model.User;
import com.example.model.SwapStatus;
import com.example.repository.AdminMessageRepository;
import com.example.repository.SwapRequestRepository;
import com.example.repository.UserRepository;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SwapRequestRepository swapRequestRepository;

    @Autowired
    private AdminMessageRepository adminMessageRepository;

    public AdminMessage sendPlatformMessage(String adminId, AdminMessageDto messageDto) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getRoles().contains("ADMIN")) {
            throw new BadRequestException("Only admins can send platform messages");
        }

        AdminMessage message = new AdminMessage();
        message.setAdmin(admin);
        message.setTitle(messageDto.getTitle());
        message.setContent(messageDto.getContent());
        message.setPriority(messageDto.getPriority());

        return adminMessageRepository.save(message);
    }

    public List<AdminMessage> getAllPlatformMessages() {
        return adminMessageRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<SwapRequest> getAllSwapRequests() {
        return swapRequestRepository.findAll();
    }

    public User banUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        return userRepository.save(user);
    }

    public User unbanUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(true);
        return userRepository.save(user);
    }

    public void deleteSwapRequest(String swapId) {
        SwapRequest swapRequest = swapRequestRepository.findById(swapId)
                .orElseThrow(() -> new ResourceNotFoundException("Swap request not found"));
        swapRequestRepository.delete(swapRequest);
    }

    public User makeUserAdmin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getRoles().contains("ADMIN")) {
            user.getRoles().add("ADMIN");
        }
        return userRepository.save(user);
    }

    public User removeAdminRole(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.getRoles().remove("ADMIN");
        return userRepository.save(user);
    }

    public void rejectSkillDescription(String userId, String skillName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.getOfferedSkills().removeIf(skill -> skill.getName().equalsIgnoreCase(skillName));
        userRepository.save(user);
    }

    public AdminDashboardStats getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeSwaps = swapRequestRepository.countByStatus(SwapStatus.ACCEPTED);
        long pendingSwaps = swapRequestRepository.countByStatus(SwapStatus.PENDING);
        long completedSwaps = swapRequestRepository.countByStatus(SwapStatus.COMPLETED);

        AdminDashboardStats stats = new AdminDashboardStats();
        stats.setTotalUsers(totalUsers);
        stats.setActiveSwaps(activeSwaps);
        stats.setPendingSwaps(pendingSwaps);
        stats.setCompletedSwaps(completedSwaps);
        return stats;
    }
}