package vn.bachhoa.dao;

import vn.bachhoa.model.Promotion;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;


public class PromotionDAO {

    public Promotion findById(int id) {
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

    public int create(Promotion p, List<Integer> categoryIds) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(p);
            em.flush(); // ensure id generated
            Integer promoId = p.getId();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                // insert mapping rows via native SQL
                for (Integer catId : categoryIds) {
                    em.createNativeQuery("INSERT INTO promotion_categories (promotionId, categoryId) VALUES (?, ?)")
                        .setParameter(1, promoId)
                        .setParameter(2, catId)
                        .executeUpdate();
                }
            }
            tx.commit();
            return promoId;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void update(Promotion p, List<Integer> categoryIds) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(p);
            // replace mappings
            em.createNativeQuery("DELETE FROM promotion_categories WHERE promotionId = ?")
                .setParameter(1, p.getId())
                .executeUpdate();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                for (Integer catId : categoryIds) {
                    em.createNativeQuery("INSERT INTO promotion_categories (promotionId, categoryId) VALUES (?, ?)")
                        .setParameter(1, p.getId())
                        .setParameter(2, catId)
                        .executeUpdate();
                }
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void delete(int id) throws Exception {
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

    public List<Promotion> listActiveForCategory(int categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String sql = "SELECT p.* FROM promotions p JOIN promotion_categories pc ON p.id = pc.promotionId " +
                         "WHERE pc.categoryId = :catId AND p.active = 1 AND p.startAt <= NOW() AND p.endAt >= NOW() ORDER BY p.startAt";
            @SuppressWarnings("unchecked")
            List<Promotion> list = em.createNativeQuery(sql, Promotion.class)
                    .setParameter("catId", categoryId)
                    .getResultList();
            return list;
        } finally {
            em.close();
        }
    }

    public List<Integer> loadCategoryIds(int promotionId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> cats = em.createNativeQuery("SELECT categoryId FROM promotion_categories WHERE promotionId = ?")
                    .setParameter(1, promotionId)
                    .getResultList();
            return new ArrayList<>(cats);
        } finally {
            em.close();
        }
    }
}


/**
 * PromotionDAO: CRUD using JPA EntityManager.
 * Note: category mappings (promotion_categories) handled with native SQL here.
 */