package com.example.repository;
import com.example.model.User;
import com.example.model.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByIsPublicTrueAndIsActiveTrue();
    List<User> findByRoles(UserRole role);

    @Query("{'offeredSkills.name': {$regex: ?0, $options: 'i'}, 'isPublic': true, 'isActive': true}")
    List<User> findByOfferedSkillsContainingIgnoreCase(String skill);

    @Query("{'wantedSkills': {$regex: ?0, $options: 'i'}, 'isPublic': true, 'isActive': true}")
    List<User> findByWantedSkillsContainingIgnoreCase(String skill);

    @Query("{'location': {$regex: ?0, $options: 'i'}, 'isPublic': true, 'isActive': true}")
    List<User> findByLocationContainingIgnoreCase(String location);
}
