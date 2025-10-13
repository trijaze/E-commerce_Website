package vn.bachhoa.dao;

import vn.bachhoa.model.Promotion;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.EntityTransaction;
import java.util.List;

public class PromotionDAO {

    public Promotion create(Promotion p) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(p);
            tx.commit();
            return p;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Promotion update(Promotion p) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Promotion merged = em.merge(p);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Promotion p = em.find(Promotion.class, id);
            if (p != null) em.remove(p);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Promotion findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Promotion.class, id);
        } finally {
            em.close();
        }
    }

    public Promotion findByCode(String code) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Promotion> q = em.createQuery("SELECT p FROM Promotion p WHERE p.code = :code AND p.active = true", Promotion.class);
            q.setParameter("code", code);
            List<Promotion> list = q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    public List<Promotion> listAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Promotion> q = em.createQuery("SELECT p FROM Promotion p ORDER BY p.startAt DESC", Promotion.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    // active for category (current time check)
    public List<Promotion> listActiveForCategory(Integer categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Promotion> q = em.createQuery(
                "SELECT DISTINCT p FROM Promotion p JOIN p.categories c WHERE c.categoryId = :catId AND p.active = true AND (p.startAt IS NULL OR p.startAt <= CURRENT_TIMESTAMP) AND (p.endAt IS NULL OR p.endAt >= CURRENT_TIMESTAMP) ORDER BY p.startAt",
                Promotion.class);
            q.setParameter("catId", categoryId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Promotion> listActiveForProduct(Integer productId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Promotion> q = em.createQuery(
                "SELECT DISTINCT p FROM Promotion p JOIN p.products pr WHERE pr.productId = :pid AND p.active = true AND (p.startAt IS NULL OR p.startAt <= CURRENT_TIMESTAMP) AND (p.endAt IS NULL OR p.endAt >= CURRENT_TIMESTAMP) ORDER BY p.startAt",
                Promotion.class);
            q.setParameter("pid", productId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Promotion> listActiveForVariant(Integer variantId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Promotion> q = em.createQuery(
                "SELECT DISTINCT p FROM Promotion p JOIN p.variants v WHERE v.variantId = :vid AND p.active = true AND (p.startAt IS NULL OR p.startAt <= CURRENT_TIMESTAMP) AND (p.endAt IS NULL OR p.endAt >= CURRENT_TIMESTAMP) ORDER BY p.startAt",
                Promotion.class);
            q.setParameter("vid", variantId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}
