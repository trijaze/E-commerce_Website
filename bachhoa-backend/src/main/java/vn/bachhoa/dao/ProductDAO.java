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

    /** Lấy tất cả sản phẩm, preload images & variants */
    public List<Product> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql =
                "SELECT p FROM Product p " +
                "LEFT JOIN FETCH p.category " +
                "LEFT JOIN FETCH p.supplier " +
                "ORDER BY p.productId DESC";

            List<Product> products = em.createQuery(jpql, Product.class).getResultList();
            products.forEach(p -> {
                if (p.getImages()   != null) p.getImages().size();
                if (p.getVariants() != null) p.getVariants().size();
            });
            return products;
        } finally {
            em.close();
        }
    }

    /** Lấy theo categoryId */
    public List<Product> findByCategoryId(int categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql =
                "SELECT p FROM Product p " +
                "LEFT JOIN FETCH p.category " +
                "LEFT JOIN FETCH p.supplier " +
                "WHERE p.category.categoryId = :categoryId " +
                "ORDER BY p.productId DESC";

            TypedQuery<Product> q = em.createQuery(jpql, Product.class);
            q.setParameter("categoryId", categoryId);

            List<Product> products = q.getResultList();
            products.forEach(p -> {
                if (p.getImages()   != null) p.getImages().size();
                if (p.getVariants() != null) p.getVariants().size();
            });
            return products;
        } finally {
            em.close();
        }
    }

    /** Lấy chi tiết 1 sản phẩm (preload đủ quan hệ) */
    public Product findDetailById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Product p = em.find(Product.class, id);
            if (p != null) {
                if (p.getCategory() != null) p.getCategory().getCategoryId();
                if (p.getSupplier() != null) p.getSupplier().getSupplierId();
                if (p.getImages()   != null) p.getImages().size();
                if (p.getVariants() != null) p.getVariants().size();
            }
            return p;
        } finally {
            em.close();
        }
    }
}
