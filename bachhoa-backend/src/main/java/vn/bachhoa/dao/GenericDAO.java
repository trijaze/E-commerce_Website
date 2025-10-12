package vn.bachhoa.dao;

import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.function.Function;

public class GenericDAO {
    protected <R> R tx(Function<EntityManager, R> fn) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = fn.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    protected <T> List<T> query(EntityManager em, Class<T> clazz, String jpql, Object... params) {
        var q = em.createQuery(jpql, clazz);
        for (int i = 0; i < params.length; i++) q.setParameter(i + 1, params[i]);
        return q.getResultList();
    }
}
