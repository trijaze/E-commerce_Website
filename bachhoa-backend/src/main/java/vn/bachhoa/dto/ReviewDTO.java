package vn.bachhoa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import vn.bachhoa.model.Review;
import java.time.LocalDateTime;

// Chỉ serialize các trường không null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDTO {
    private Integer reviewId;
    private int rating;
    private String title;
    private String comment;
    private LocalDateTime createdAt;
    private String username; // Tên người viết review

    public ReviewDTO() {}

    // Constructor để chuyển từ Entity sang DTO
    public ReviewDTO(Review review) {
        this.reviewId = review.getReviewID();
        this.rating = review.getRating();
        this.title = review.getTitle();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
        if (review.getUser() != null) {
            this.username = review.getUser().getUsername();
        }
    }
    
    // Getters and Setters
    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}