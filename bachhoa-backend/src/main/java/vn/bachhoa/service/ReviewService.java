package vn.bachhoa.service;

import vn.bachhoa.dao.ReviewDAO;
import vn.bachhoa.dao.UserDAO;
import vn.bachhoa.dto.ReviewDTO;
import vn.bachhoa.model.Product;
import vn.bachhoa.model.Review;
import vn.bachhoa.model.User;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewService {
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final UserDAO userDAO = new UserDAO();
    // Giả sử bạn có một ProductDAO tương tự hoặc dùng trực tiếp EntityManager
    // Ở đây tôi sẽ dùng EntityManager để tìm Product cho đơn giản
    
    /**
     * Lấy tất cả các review đã được duyệt cho một sản phẩm.
     * @param productId ID của sản phẩm.
     * @return Danh sách ReviewDTO.
     */
    public List<ReviewDTO> getReviewsForProduct(int productId) {
        return reviewDAO.findByProductId(productId).stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Thêm một review mới cho sản phẩm.
     * @param userId ID của người dùng viết review.
     * @param productId ID của sản phẩm được review.
     * @param reviewData DTO chứa thông tin review.
     * @return DTO của review vừa được tạo.
     * @throws IllegalArgumentException nếu user hoặc product không tồn tại.
     */
    public ReviewDTO addReview(int userId, int productId, ReviewDTO reviewData) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Dùng EntityManager để tìm Product vì chưa có ProductDAO
        EntityManager em = JPAUtil.getEntityManager();
        Product product;
        try {
            product = em.find(Product.class, productId);
            if (product == null) {
                throw new IllegalArgumentException("Product not found");
            }
        } finally {
            em.close();
        }

        Review newReview = new Review();
        newReview.setUser(user);
        newReview.setProduct(product);
        newReview.setRating(reviewData.getRating());
        newReview.setTitle(reviewData.getTitle());
        newReview.setComment(reviewData.getComment());
        // isApproved và createdAt sẽ được tự động set bởi @PrePersist

        Review savedReview = reviewDAO.save(newReview);
        
        return new ReviewDTO(savedReview);
    }
}