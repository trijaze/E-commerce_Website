package vn.bachhoa.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(length = 50) private String action;
    @Column(length = 50) private String targetTable;
    @Column(length = 50) private String targetId;
    private LocalDateTime createdAt = LocalDateTime.now();
}
