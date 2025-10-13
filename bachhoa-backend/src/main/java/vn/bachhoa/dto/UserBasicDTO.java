package vn.bachhoa.dto;

import java.time.LocalDateTime;

public class UserBasicDTO {
    private int userId;
    private String username;
    private String phoneNumber;
    private String passwordHash;
    private String email;
    private LocalDateTime createdAt;

    public UserBasicDTO(int userId, String username, String passwordHash, String phoneNumber, String email, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash;}
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
