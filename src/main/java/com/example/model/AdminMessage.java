// com.example.model.AdminMessage.java
package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "admin_messages")
public class AdminMessage {
    @Id
    private String id;
    private User admin; // Reference to the admin user who sent the message
    private String title;
    private String content;
    private String priority; // e.g., "LOW", "MEDIUM", "HIGH"
    private LocalDateTime createdAt;
    private boolean isActive;

    // Constructors
    public AdminMessage() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public AdminMessage(User admin, String title, String content, String priority) {
        this.admin = admin;
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}