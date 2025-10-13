package vn.bachhoa.service;

import vn.bachhoa.dao.ReviewDAO;
import vn.bachhoa.dto.ReviewDTO;
import vn.bachhoa.model.Product;
import vn.bachhoa.model.Review;
import vn.bachhoa.model.User;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewService {
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public List<ReviewDTO> getReviewsForProduct(int productId) {
        return reviewDAO.findByProductId(productId).stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
    }

    public ReviewDTO addReview(int userId, int productId, ReviewDTO reviewData) {
        System.out.println("=== START addReview ===");
        System.out.println("userId: " + userId);
        System.out.println("productId: " + productId);
        System.out.println("rating: " + reviewData.getRating());
        
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            // ✅ Tìm User
            User user = em.find(User.class, userId);
            if (user == null) {
                System.err.println("❌ User not found: " + userId);
                throw new IllegalArgumentException("User not found");
            }
            System.out.println("✅ User found: " + user.getUsername());
            
            // ✅ Tìm Product
            Product product = em.find(Product.class, productId);
            if (product == null) {
                System.err.println("❌ Product not found: " + productId);
                throw new IllegalArgumentException("Product not found");
            }
            System.out.println("✅ Product found: " + product.getName());
            
            // ✅ Tạo Review
            Review newReview = new Review();
            newReview.setUser(user);
            newReview.setProduct(product);
            newReview.setRating(reviewData.getRating());
            newReview.setTitle(reviewData.getTitle());
            newReview.setComment(reviewData.getComment());
            newReview.setApproved(true); // ✅ Explicit set approved
            
            // ✅ Persist
            em.persist(newReview);
            em.flush(); // ✅ Force write to DB immediately
            
            System.out.println("✅ Review saved with ID: " + newReview.getReviewID());
            
            tx.commit();
            
            // ✅ Return DTO
            ReviewDTO result = new ReviewDTO(newReview);
            System.out.println("=== END addReview (SUCCESS) ===");
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ Error in addReview: " + e.getMessage());
            e.printStackTrace();
            
            if (tx != null && tx.isActive()) {
                tx.rollback();
                System.out.println("🔄 Transaction rolled back");
            }
            throw new RuntimeException("Failed to add review: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}