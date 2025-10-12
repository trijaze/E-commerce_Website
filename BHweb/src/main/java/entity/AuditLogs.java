package entity;

import javax.persistence.*;
import java.time.LocalDateTime; 
@Entity
@Table(name = "auditLogs")
public class AuditLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logId")
    private long logId;

    // Lưu lại ID của người dùng đã thực hiện hành động
    @Column(name = "userId")
    private int userId;

    @Column(name = "action")
    private String action; // "LOGIN", "REGISTER", "CHANGE_PASSWORD"

    @Column(name = "targetTable")
    private String targetTable; // Bảng bị ảnh hưởng, ví dụ: "Users"

    @Column(name = "targetId")
    private String targetId; // ID của bản ghi bị ảnh hưởng

    @Column(name = "createdAt")
    private LocalDateTime createdAt; // Thời điểm hành động diễn ra

    // Constructors
    public AuditLogs() {
    }

    public AuditLogs(int userId, String action, String targetTable, String targetId) {
        this.userId = userId;
        this.action = action;
        this.targetTable = targetTable;
        this.targetId = targetId;
        this.createdAt = LocalDateTime.now(); // Tự động lấy thời gian hiện tại khi tạo
    }

    // Getters and Setters
    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
