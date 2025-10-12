package dao;

import entity.AuditLogs; // Đổi tên package entity cho phù hợp
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class AuditLogDAO {
    // Tên persistence unit này phải khớp với file persistence.xml của bạn
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("UserPU");

    /**
     * Lưu một bản ghi nhật ký (AuditLog) vào cơ sở dữ liệu.
     * @param log Đối tượng AuditLog cần lưu.
     */
    public void save(AuditLogs log) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(log); // Dùng persist để lưu đối tượng mới
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace(); // In lỗi ra console để debug
        } finally {
            em.close();
        }
    }
}
