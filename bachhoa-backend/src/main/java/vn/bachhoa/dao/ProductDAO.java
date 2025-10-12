package vn.bachhoa.dao;

import vn.bachhoa.model.Product;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ProductDAO extends GenericDAO<Product> {

    public ProductDAO() {
        super(Product.class);
    }

    /**
     * Lấy tất cả sản phẩm, preload images & variants để tránh LazyInitializationException
     */
    public List<Product> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Chỉ fetch category và supplier, không fetch images & variants bằng JOIN
            String jpql = "SELECT p FROM Product p " +
                          "LEFT JOIN FETCH p.category " +
                          "LEFT JOIN FETCH p.supplier " +
                          "ORDER BY p.productId DESC";
            List<Product> products = em.createQuery(jpql, Product.class).getResultList();

            // Preload images và variants
            products.forEach(p -> {
                p.getImages().size();
                p.getVariants().size();
            });

            return products;
        } finally {
            em.close();
        }
    }

    /**
     * Lấy danh sách sản phẩm theo categoryId
     */
    public List<Product> findByCategoryId(int categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Product p " +
                          "LEFT JOIN FETCH p.category " +
                          "LEFT JOIN FETCH p.supplier " +
                          "WHERE p.category.categoryId = :categoryId " +
                          "ORDER BY p.productId DESC";

            TypedQuery<Product> query = em.createQuery(jpql, Product.class);
            query.setParameter("categoryId", categoryId);

            List<Product> products = query.getResultList();

            // Preload images & variants
            products.forEach(p -> {
                p.getImages().size();
                p.getVariants().size();
            });

            return products;
        } finally {
            em.close();
        }
    }
}
