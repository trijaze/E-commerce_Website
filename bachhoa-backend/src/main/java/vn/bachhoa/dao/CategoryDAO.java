package vn.bachhoa.dao;

import vn.bachhoa.model.Category;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class CategoryDAO extends GenericDAO<Category> {

    public CategoryDAO() {
        super(Category.class);
    }

    public List<Category> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Category c ORDER BY c.name";
            return em.createQuery(jpql, Category.class).getResultList();
        } finally {
            em.close();
        }
    }
}
