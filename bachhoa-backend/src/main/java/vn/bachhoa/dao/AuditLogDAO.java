package vn.bachhoa.dao;

import vn.bachhoa.model.AuditLog;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class AuditLogDAO {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("bachhoaPU");

    //Lưu một bản ghi nhật ký (AuditLog) vào cơ sở dữ liệu.
    public void save(AuditLog log) {
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
