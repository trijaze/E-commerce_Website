package vn.bachhoa.dao;

import vn.bachhoa.model.Product;
import vn.bachhoa.model.ProductVariant;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ProductVariantDAO extends GenericDAO<ProductVariant> {

    public ProductVariantDAO() {
        super(ProductVariant.class);
    }

    /** Lấy tất cả variants theo productId */
    public List<ProductVariant> findByProductId(int productId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ProductVariant> query = em.createQuery(
                    "SELECT v FROM ProductVariant v WHERE v.product.productId = :productId",
                    ProductVariant.class
            );
            query.setParameter("productId", productId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Tạo variant mới cho sản phẩm */
    public ProductVariant createVariant(ProductVariant variant, int productId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Tìm product để set relationship
            Product product = em.find(Product.class, productId);
            if (product == null) {
                throw new IllegalArgumentException("Product not found with id: " + productId);
            }

            variant.setProduct(product);
            em.persist(variant);
            em.getTransaction().commit();
            
            return variant;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Cập nhật variant */
    public ProductVariant updateVariant(ProductVariant variant) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductVariant merged = em.merge(variant);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Xóa variant */
    public void deleteVariant(int variantId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ProductVariant variant = em.find(ProductVariant.class, variantId);
            if (variant != null) {
                em.remove(variant);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Lấy chi tiết variant theo ID */
    public ProductVariant findVariantById(int variantId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            ProductVariant variant = em.find(ProductVariant.class, variantId);
            if (variant != null && variant.getProduct() != null) {
                // Force load product để tránh LazyInitializationException
                variant.getProduct().getProductId();
            }
            return variant;
        } finally {
            em.close();
        }
    }
}