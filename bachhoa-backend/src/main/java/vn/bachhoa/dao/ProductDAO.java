package vn.bachhoa.dao;

import vn.bachhoa.dto.ProductDTO;
import vn.bachhoa.dto.ProductDetailDTO;
import vn.bachhoa.model.Product;
import vn.bachhoa.util.JPAUtil;
import vn.bachhoa.model.ProductImage;
import vn.bachhoa.model.ProductVariant;


import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDAO extends GenericDAO<Product> {

    public ProductDAO() {
        super(Product.class);
    }

    /** Lấy danh sách sản phẩm (preload category + supplier; collections được SUBSELECT khi access) */
    public List<ProductDTO> findAllDTO() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = """
                SELECT DISTINCT p FROM Product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.supplier
                ORDER BY p.productId DESC
            """;
            List<Product> list = em.createQuery(jpql, Product.class).getResultList();

            // --- KÍCH HOẠT việc load collection bằng cách access trong cùng EntityManager mở.
            // Với @Fetch(FetchMode.SUBSELECT) ở entity, Hibernate sẽ chạy 2 subselect:
            // SELECT ... FROM productimages WHERE productId IN (...)
            // SELECT ... FROM productvariants WHERE productId IN (...)
            // và populate cả danh sách images + variants cho tất cả product trong 'list'
            for (Product p : list) {
                // access to initialize (size() is enough)
                if (p.getImages() != null) p.getImages().size();
                if (p.getVariants() != null) p.getVariants().size();
            }

            return list.stream().map(ProductDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }


    /** Lấy danh sách sản phẩm theo categoryId */
    public List<ProductDTO> findByCategoryDTO(Integer categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = """
                SELECT DISTINCT p FROM Product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.supplier
                WHERE p.category.categoryId = :cid
                ORDER BY p.productId DESC
            """;
            List<Product> list = em.createQuery(jpql, Product.class)
                    .setParameter("cid", categoryId)
                    .getResultList();

            // initialize collections using SUBSELECT (runs subselects once for the whole list)
            for (Product p : list) {
                if (p.getImages() != null) p.getImages().size();
                if (p.getVariants() != null) p.getVariants().size();
            }

            return list.stream().map(ProductDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public ProductDetailDTO findDetailDTOById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Lấy product + category + supplier
            String jpql1 = """
                SELECT DISTINCT p FROM Product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.supplier
                WHERE p.productId = :id
            """;
            Product p = em.createQuery(jpql1, Product.class)
                          .setParameter("id", id)
                          .getSingleResult();

            // Lấy images
            p.setImages(em.createQuery(
                "SELECT i FROM ProductImage i WHERE i.product.productId = :id ORDER BY i.imageId", ProductImage.class)
                .setParameter("id", id)
                .getResultList());

            // Lấy variants
            p.setVariants(em.createQuery(
                "SELECT v FROM ProductVariant v WHERE v.product.productId = :id ORDER BY v.variantId", ProductVariant.class)
                .setParameter("id", id)
                .getResultList());

            return new ProductDetailDTO(p);
        } finally {
            em.close();
        }
    }


    /** Lấy sản phẩm liên quan (cùng category) */
    public List<ProductDTO> findRelatedDTO(Integer productId, int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Integer catId = em.createQuery(
                    "SELECT p.category.categoryId FROM Product p WHERE p.productId = :id", Integer.class)
                    .setParameter("id", productId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (catId == null) return Collections.emptyList();

            String jpql = """
                SELECT DISTINCT p FROM Product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.supplier
                WHERE p.category.categoryId = :cid AND p.productId <> :id
                ORDER BY p.productId DESC
            """;

            List<Product> list = em.createQuery(jpql, Product.class)
                    .setParameter("cid", catId)
                    .setParameter("id", productId)
                    .setMaxResults(Math.max(1, limit))
                    .getResultList();

            // initialize to trigger SUBSELECT for images + variants (batch for the whole list)
            for (Product p : list) {
                if (p.getImages() != null) p.getImages().size();
                if (p.getVariants() != null) p.getVariants().size();
            }

            return list.stream().map(ProductDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    /** Tạo sản phẩm mới (từ demo) */
    public Product createProduct(Product product) {
        return createProduct(product, 0); // Default với stock = 0
    }

    /** Tạo sản phẩm mới với stock cho default variant */
    public Product createProduct(Product product, Integer stockQuantity) {
        return createProduct(product, stockQuantity, null);
    }

    /** Tạo sản phẩm mới với stock và image */
    public Product createProduct(Product product, Integer stockQuantity, String imageUrl) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Persist product trước
            em.persist(product);
            em.flush(); // Đảm bảo productId được generate
            
            // Tạo default ProductVariant nếu có stockQuantity > 0
            if (stockQuantity != null && stockQuantity > 0) {
                ProductVariant defaultVariant = new ProductVariant();
                defaultVariant.setProduct(product);
                defaultVariant.setVariantSku(product.getSku() + "-DEFAULT");
                defaultVariant.setAttributes("Mặc định");
                defaultVariant.setPrice(product.getBasePrice());
                defaultVariant.setStockQuantity(stockQuantity);
                
                em.persist(defaultVariant);
            }
            
            // Tạo ProductImage nếu có imageUrl
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(imageUrl.trim());
                image.setIsMain(true); // Đây là ảnh chính
                
                em.persist(image);
            }
            
            // Refresh để load lại variants và images
            em.flush();
            em.refresh(product);
            
            em.getTransaction().commit();
            return product;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Cập nhật sản phẩm (từ demo) */
    public Product updateProduct(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Product merged = em.merge(product);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Xóa sản phẩm (từ demo) */
    public void deleteProduct(int productId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Product product = em.find(Product.class, productId);
            if (product != null) {
                em.remove(product);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Lấy tất cả sản phẩm với chi tiết đầy đủ cho admin */
    public List<ProductDetailDTO> findAllDetailDTO() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = """
                SELECT DISTINCT p FROM Product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.supplier
                ORDER BY p.productId DESC
            """;
            List<Product> list = em.createQuery(jpql, Product.class).getResultList();

            // Load images và variants cho từng product
            for (Product p : list) {
                if (p.getImages() != null) p.getImages().size();
                if (p.getVariants() != null) p.getVariants().size();
            }

            return list.stream().map(ProductDetailDTO::new).collect(java.util.stream.Collectors.toList());
        } finally {
            em.close();
        }
    }

}
