package vn.bachhoa.dao;

import vn.bachhoa.model.Product;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class ProductDAO extends GenericDAO {

    public List<Product> findAll(int page, int size, String q, Integer categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Product p " +
                    (q != null && !q.isEmpty() ? "WHERE lower(p.name) LIKE lower(:q) " : "") +
                    (categoryId != null ? (q != null && !q.isEmpty() ? "AND " : "WHERE ") + "p.category.categoryId = :cid " : "") +
                    "ORDER BY p.productId DESC";

            var query = em.createQuery(jpql, Product.class);
            if (q != null && !q.isEmpty()) query.setParameter("q", "%" + q + "%");
            if (categoryId != null) query.setParameter("cid", categoryId);
            query.setFirstResult(Math.max(0, (page - 1) * size));
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Product findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }
}
