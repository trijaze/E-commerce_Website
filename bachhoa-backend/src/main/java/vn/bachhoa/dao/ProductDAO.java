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
            // Fetch category và supplier (không JOIN collection để tránh nhân bản dòng)
            String jpql = "SELECT p FROM Product p " +
                          "LEFT JOIN FETCH p.category " +
                          "LEFT JOIN FETCH p.supplier " +
                          "ORDER BY p.productId DESC";
            List<Product> products = em.createQuery(jpql, Product.class).getResultList();

            // Preload images & variants để dùng được sau khi đóng EM
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

    /**
     * Lấy chi tiết sản phẩm theo id, đảm bảo preload:
     * - category, supplier (đụng vào khóa để init)
     * - images, variants (size() để init collection)
     */
    public Product findDetailById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Product p = em.find(Product.class, id);
            if (p != null) {
                // chạm vào quan hệ để initialize trước khi đóng EM
                if (p.getCategory() != null) {
                    p.getCategory().getCategoryId();
                }
                if (p.getSupplier() != null) {
                    p.getSupplier().getSupplierId();
                }
                p.getImages().size();
                p.getVariants().size();
            }
            return p;
        } finally {
            em.close();
        }
    }
}
