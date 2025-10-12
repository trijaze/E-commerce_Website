package vn.bachhoa.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role = "customer"; // admin, customer

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> addresses = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
