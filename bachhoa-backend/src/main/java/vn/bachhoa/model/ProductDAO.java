package vn.bachhoa.model;

import vn.bachhoa.model.Product;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import vn.bachhoa.dao.GenericDAO;

public class ProductDAO extends GenericDAO<Product> {

    public ProductDAO() {
        super(Product.class);
    }

    public List<Product> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT p FROM Product p " +
                          "LEFT JOIN FETCH p.category " +
                          "LEFT JOIN FETCH p.supplier " +
                          "LEFT JOIN FETCH p.images " +     // thêm dòng này
                          "LEFT JOIN FETCH p.variants " +   // thêm dòng này
                          "ORDER BY p.productId DESC";

            return em.createQuery(jpql, Product.class).getResultList();
        } finally {
            em.close();
        }
    }


    public List<Product> findByCategoryId(int categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT p FROM Product p " +
                          "LEFT JOIN FETCH p.category " +
                          "LEFT JOIN FETCH p.supplier " +
                          "LEFT JOIN FETCH p.images " +     // thêm dòng này
                          "LEFT JOIN FETCH p.variants " +   // thêm dòng này
                          "WHERE p.category.categoryId = :categoryId " +
                          "ORDER BY p.productId DESC";

            TypedQuery<Product> query = em.createQuery(jpql, Product.class);
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

}
