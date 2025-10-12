package vn.bachhoa.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf = build();

    private static EntityManagerFactory build() {
        try {
            return Persistence.createEntityManagerFactory("bachhoaPU");
        } catch (Throwable t) {
            System.err.println("Initial EntityManagerFactory creation failed: " + t);
            throw new ExceptionInInitializerError(t);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
