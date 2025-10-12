package vn.bachhoa.dao;

import vn.bachhoa.model.Promotion;
import vn.bachhoa.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PromotionDAO {
    public List<Promotion> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        return em.createQuery("FROM Promotion", Promotion.class).getResultList();
    }

    public Promotion findByCode(String code) {
        EntityManager em = JPAUtil.getEntityManager();
        return em.createQuery("FROM Promotion WHERE code = :code", Promotion.class)
                .setParameter("code", code)
                .getSingleResult();
    }

    public void save(Promotion promotion) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(promotion);
        em.getTransaction().commit();
    }
}
