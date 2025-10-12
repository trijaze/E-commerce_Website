package vn.bachhoa.dao;

import vn.bachhoa.model.Review;
import javax.persistence.TypedQuery;
import java.util.List;

public class ReviewDAO extends GenericDAO<Review> {
    public ReviewDAO() {
        super(Review.class);
    }

    /**
     * Lưu một review mới vào cơ sở dữ liệu.
     * @param review Đối tượng review cần lưu.
     * @return Đối tượng review đã được lưu (với ID được gán).
     */
    public Review save(Review review) {
        return tx(em -> {
            em.persist(review);
            return review;
        });
    }

    /**
     * Tìm tất cả các review của một sản phẩm cụ thể.
     * @param productId ID của sản phẩm.
     * @return Danh sách các review, sắp xếp theo ngày tạo mới nhất.
     */
    public List<Review> findByProductId(int productId) {
        return tx(em -> {
            TypedQuery<Review> query = em.createQuery(
                "SELECT r FROM Review r WHERE r.product.productId = :productId AND r.isApproved = true ORDER BY r.createdAt DESC",
                Review.class
            );
            query.setParameter("productId", productId);
            return query.getResultList();
        });
    }
    
    // Bạn có thể thêm các phương thức khác như update, delete nếu cần
}