package vn..bachhoa.dao;

import javax.persistence.*;
import vn.nhom7.bachhoa.model.Product;
import java.util.List;

public class ProductRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bachhoaPU");

    public List<Product> filterProducts(String keyword, Integer categoryId, Integer supplierId,
                                        String priceRange, Double minPrice, Double maxPrice, String sort) {

        EntityManager em = emf.createEntityManager();
        StringBuilder jpql = new StringBuilder
                ("SELECT DISTINCT p FROM Product p " +
                        "LEFT JOIN FETCH p.category c " +
                        "LEFT JOIN FETCH p.supplier s " +
                        "LEFT JOIN FETCH p.images i " +
                        "WHERE 1=1 "
                );

        if (keyword != null && !keyword.isEmpty())
            jpql.append("AND LOWER(p.name) LIKE LOWER(:keyword) ");
        if (categoryId != null)
            jpql.append("AND p.category.categoryId = :categoryId ");
        if (supplierId != null)
            jpql.append("AND p.supplier.supplierId = :supplierId ");

        // Giá nhanh
        if (priceRange != null) {
            switch (priceRange) {
                case "low" -> jpql.append("AND p.basePrice < 50000 ");
                case "middle" -> jpql.append("AND p.basePrice BETWEEN 50000 AND 100000 ");
                case "high" -> jpql.append("AND p.basePrice BETWEEN 100000 AND 300000 ");
                case "premium" -> jpql.append("AND p.basePrice > 300000 ");
            }
        }

        // Sắp xếp
        switch (sort == null ? "" : sort) {
            case "price_asc" -> jpql.append("ORDER BY p.basePrice ASC");
            case "price_desc" -> jpql.append("ORDER BY p.basePrice DESC");
            case "name_az" -> jpql.append("ORDER BY p.name ASC");
            case "name_za" -> jpql.append("ORDERBY p.name DESC");
            default -> jpql.append("ORDER BY p.productId ASC");
        }

        TypedQuery<Product> query = em.createQuery(jpql.toString(), Product.class);

        if (keyword != null && !keyword.isEmpty())
            query.setParameter("keyword", "%" + keyword + "%");
        if (categoryId != null)
            query.setParameter("categoryId", categoryId);
        if (supplierId != null)
            query.setParameter("supplierId", supplierId);

        List<Product> result = query.getResultList();
        em.close();
        return result;
    }
}
