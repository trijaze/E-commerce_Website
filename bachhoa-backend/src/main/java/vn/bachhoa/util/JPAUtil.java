package vn.bachhoa.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {
    private static final String PERSISTENCE_UNIT_NAME = "bachhoaPU";

    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Throwable ex) {
            System.err.println("Lỗi khi khởi tạo EntityManagerFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Lấy EntityManager để thao tác database
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Đóng EntityManagerFactory khi ứng dụng kết thúc
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
