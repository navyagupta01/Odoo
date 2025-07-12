package com.example.dto;

public class AuthResponseDto {
    private String token;
    private String type = "Bearer";
    private UserProfileDto user;

    public AuthResponseDto() {}

    public AuthResponseDto(String token, UserProfileDto user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public UserProfileDto getUser() { return user; }
    public void setUser(UserProfileDto user) { this.user = user; }
}
