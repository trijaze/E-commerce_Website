package entity;

import javax.persistence.*;
import java.time.LocalDateTime; 

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String phone;
    
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String role = "user"; // Giá trị mặc định

    // updatable = false để đảm bảo cột này chỉ được đặt giá trị một lần khi tạo.
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public User() {}

    public User(String username, String password, String phone, String email) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
    }

    // Hàm này sẽ tự động được gọi bởi JPA ngay trước khi lưu User lần đầu tiên.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // --- Getters and Setters cho các trường cũ ---
    // (id, username, password, phone, role)
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

