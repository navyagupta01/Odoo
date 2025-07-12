// com.skillswap.service.UserService.java
package com.example.service;

import com.example.model.SkillLevel;
import com.example.model.User;
import com.example.model.ProfileStats;
import com.example.model.Skill;
import com.example.dto.UserRegistrationDto;
import com.example.dto.UserProfileDto;
import com.example.dto.SearchDto;
import com.example.repository.UserRepository;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setName(registrationDto.getName());
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setLocation(registrationDto.getLocation());
        user.setTagline(registrationDto.getTagline());
        user.setPublic(registrationDto.isPublic());
        user.setOfferedSkills(registrationDto.getOfferedSkills());
        user.setWantedSkills(registrationDto.getWantedSkills());
        user.setAvailability(registrationDto.getAvailability());
        user.setProfilePhoto(registrationDto.getProfilePhoto());
        user.setStats(new ProfileStats());

        return userRepository.save(user);
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserProfileDto getUserProfile(String id) {
        User user = findById(id);
        return convertToDto(user, null);
    }

    public UserProfileDto getUserProfileWithMatch(String id, String currentUserId) {
        User user = findById(id);
        User currentUser = findById(currentUserId);
        int matchPercentage = calculateMatchPercentage(currentUser, user);
        return convertToDto(user, matchPercentage);
    }

    public User updateProfile(String id, UserProfileDto profileDto) {
        User user = findById(id);

        user.setName(profileDto.getName());
        user.setLocation(profileDto.getLocation());
        user.setTagline(profileDto.getTagline());
        user.setPublic(profileDto.isPublic());
        user.setOfferedSkills(profileDto.getOfferedSkills());
        user.setWantedSkills(profileDto.getWantedSkills());
        user.setAvailability(profileDto.getAvailability());
        user.setProfilePhoto(profileDto.getProfilePhoto());

        return userRepository.save(user);
    }

    public List<UserProfileDto> searchUsers(SearchDto searchDto) {
        List<User> users;
        if (searchDto.getSkill() != null && !searchDto.getSkill().isEmpty()) {
            if (searchDto.isWantedSkills()) {
                users = userRepository.findByWantedSkillsContainingIgnoreCase(searchDto.getSkill());
            } else {
                users = userRepository.findByOfferedSkillsContainingIgnoreCase(searchDto.getSkill());
            }
        } else if (searchDto.getLocation() != null && !searchDto.getLocation().isEmpty()) {
            users = userRepository.findByLocationContainingIgnoreCase(searchDto.getLocation());
        } else {
            users = userRepository.findByIsPublicTrueAndIsActiveTrue();
        }

        return users.stream()
                .map(user -> convertToDto(user, null))
                .collect(Collectors.toList());
    }

    public List<UserProfileDto> getAllPublicUsers() {
        return userRepository.findByIsPublicTrueAndIsActiveTrue()
                .stream()
                .map(user -> convertToDto(user, null))
                .collect(Collectors.toList());
    }

    public List<UserProfileDto> getPotentialMatches(String userId) {
        User currentUser = findById(userId);
        return userRepository.findByIsPublicTrueAndIsActiveTrue()
                .stream()
                .filter(user -> !user.getId().equals(userId))
                .map(user -> convertToDto(user, calculateMatchPercentage(currentUser, user)))
                .sorted((a, b) -> Integer.compare(b.getStats().getMatchPercentage(), a.getStats().getMatchPercentage()))
                .collect(Collectors.toList());
    }

    public void incrementProfileViews(String userId) {
        User user = findById(userId);
        user.getStats().setProfileViews(user.getStats().getProfileViews() + 1);
        userRepository.save(user);
    }

    public void validateSkills(List<Skill> offeredSkills, List<String> wantedSkills) {
        if (offeredSkills != null) {
            for (Skill skill : offeredSkills) {
                if (skill.getName() == null || skill.getName().trim().isEmpty()) {
                    throw new BadRequestException("Skill name cannot be empty");
                }
            }
        }
        if (wantedSkills != null) {
            for (String skill : wantedSkills) {
                if (skill == null || skill.trim().isEmpty()) {
                    throw new BadRequestException("Wanted skill cannot be empty");
                }
            }
        }
    }

    private UserProfileDto convertToDto(User user, Integer matchPercentage) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setLocation(user.getLocation());
        dto.setProfilePhoto(user.getProfilePhoto());
        dto.setTagline(user.getTagline());
        dto.setPublic(user.isPublic());
        dto.setOfferedSkills(user.getOfferedSkills());
        dto.setWantedSkills(user.getWantedSkills());
        dto.setAvailability(user.getAvailability());
        ProfileStats stats = user.getStats();
        if (matchPercentage != null) {
            stats.setMatchPercentage(matchPercentage);
        }
        dto.setStats(stats);
        return dto;
    }

    private int calculateMatchPercentage(User currentUser, User otherUser) {
        int totalPoints = 0;
        int maxPoints = 100;

        // Skill match: 50 points max
        int skillMatchPoints = 0;
        List<String> currentWanted = currentUser.getWantedSkills() != null ? currentUser.getWantedSkills() : List.of();
        List<Skill> otherOffered = otherUser.getOfferedSkills() != null ? otherUser.getOfferedSkills() : List.of();
        List<String> currentOffered = currentUser.getOfferedSkills() != null
                ? currentUser.getOfferedSkills().stream().map(Skill::getName).collect(Collectors.toList())
                : List.of();
        List<String> otherWanted = otherUser.getWantedSkills() != null ? otherUser.getWantedSkills() : List.of();

        for (String wanted : currentWanted) {
            for (Skill offered : otherOffered) {
                if (offered.getName().equalsIgnoreCase(wanted)) {
                    skillMatchPoints += offered.getLevel() == SkillLevel.EXPERT ? 20 : offered.getLevel() == SkillLevel.INTERMEDIATE ? 15 : 10;
                }
            }
        }
        for (String wanted : otherWanted) {
            if (currentOffered.stream().anyMatch(offered -> offered.equalsIgnoreCase(wanted))) {
                skillMatchPoints += 10;
            }
        }
        skillMatchPoints = Math.min(skillMatchPoints, 50);
        totalPoints += skillMatchPoints;

        // Location match: 20 points max
        if (currentUser.getLocation() != null && otherUser.getLocation() != null &&
                currentUser.getLocation().equalsIgnoreCase(otherUser.getLocation())) {
            totalPoints += 20;
        }

        // Rating match: 20 points max
        double rating = otherUser.getStats().getAverageRating();
        totalPoints += rating > 0 ? (int) (rating * 4) : 0;

        // Activity match: 10 points max
        if (otherUser.getStats().getTotalSwaps() > 0) {
            totalPoints += Math.min(otherUser.getStats().getTotalSwaps(), 10);
        }

        return Math.min(totalPoints, maxPoints);
    }
}