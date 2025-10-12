package vn.bachhoa.dao;

import vn.bachhoa.model.Product;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class ProductDAO extends GenericDAO<Product> {

    public ProductDAO() {
        super(Product.class);
    }

    /**
     * Lấy chi tiết 1 sản phẩm và nạp đủ quan hệ:
     * - B1: nạp category, supplier (QUAN HỆ ĐƠN)
     * - B2: nạp images (BAG #1)
     * - B3: nạp variants (BAG #2)
     *
     * Cách này tránh MultipleBagFetchException khi có 2 collection.
     */
    public Product findByIdWithRelations(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // B1: fetch entity + quan hệ đơn
            Product p = em.createQuery(
                    "SELECT p FROM Product p " +
                    "LEFT JOIN FETCH p.category " +
                    "LEFT JOIN FETCH p.supplier " +
                    "WHERE p.productId = :id", Product.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);

            if (p == null) return null;

            // B2: fetch images
            em.createQuery(
                    "SELECT p FROM Product p " +
                    "LEFT JOIN FETCH p.images " +
                    "WHERE p = :p", Product.class)
              .setParameter("p", p)
              .getSingleResult();

            // B3: fetch variants
            em.createQuery(
                    "SELECT p FROM Product p " +
                    "LEFT JOIN FETCH p.variants " +
                    "WHERE p = :p", Product.class)
              .setParameter("p", p)
              .getSingleResult();

            return p;
        } finally {
            em.close();
        }
    }

    /**
     * Sản phẩm liên quan: cùng category với sản phẩm gốc (trừ chính nó).
     * Preload images để FE có thumbnail.
     */
    public List<Product> findRelated(Integer productId, int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Tìm categoryId của sản phẩm gốc
            Integer catId = em.createQuery(
                    "SELECT p.category.categoryId FROM Product p WHERE p.productId = :id", Integer.class)
                .setParameter("id", productId)
                .getResultStream()
                .findFirst()
                .orElse(null);

            if (catId == null) return Collections.emptyList();

            // Lấy danh sách liên quan + preload images (chỉ 1 bag -> an toàn)
            String jpql =
                "SELECT DISTINCT p FROM Product p " +
                "LEFT JOIN FETCH p.images " +
                "WHERE p.category.categoryId = :catId AND p.productId <> :id " +
                "ORDER BY p.productId DESC";

            TypedQuery<Product> q = em.createQuery(jpql, Product.class)
                .setParameter("catId", catId)
                .setParameter("id", productId)
                .setMaxResults(Math.max(1, limit));

            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * (Tuỳ chọn) Danh sách có tìm kiếm + lọc category + phân trang cơ bản.
     * Chỉ preload images để show thumbnail (không preload variants để tránh 2 bag).
     */
    public List<Product> findAll(int page, int size, String q, Integer categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder sb = new StringBuilder(
                "SELECT DISTINCT p FROM Product p " +
                "LEFT JOIN FETCH p.images "
            );

            boolean hasQ = q != null && !q.isBlank();
            boolean hasCat = categoryId != null;

            if (hasQ || hasCat) sb.append("WHERE ");
            if (hasQ) sb.append("LOWER(p.name) LIKE LOWER(:kw) ");
            if (hasCat) {
                if (hasQ) sb.append("AND ");
                sb.append("p.category.categoryId = :cid ");
            }
            sb.append("ORDER BY p.productId DESC");

            TypedQuery<Product> query = em.createQuery(sb.toString(), Product.class);

            if (hasQ)  query.setParameter("kw", "%" + q.trim() + "%");
            if (hasCat) query.setParameter("cid", categoryId);

            if (page >= 0 && size > 0) {
                query.setFirstResult(page * size);
                query.setMaxResults(size);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * (Tuỳ chọn) Đếm tổng record để phân trang phía FE.
     */
    public long countAll(String q, Integer categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder sb = new StringBuilder("SELECT COUNT(p) FROM Product p ");
            boolean hasQ = q != null && !q.isBlank();
            boolean hasCat = categoryId != null;

            if (hasQ || hasCat) sb.append("WHERE ");
            if (hasQ) sb.append("LOWER(p.name) LIKE LOWER(:kw) ");
            if (hasCat) {
                if (hasQ) sb.append("AND ");
                sb.append("p.category.categoryId = :cid ");
            }

            TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
            if (hasQ)  query.setParameter("kw", "%" + q.trim() + "%");
            if (hasCat) query.setParameter("cid", categoryId);

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
