package com.example.repository;

import com.example.model.AdminMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMessageRepository extends MongoRepository<AdminMessage, String> {
    List<AdminMessage> findByIsActiveTrueOrderByCreatedAtDesc();
}
