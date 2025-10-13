package vn.bachhoa.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewID;

    private int rating;
    private String title;
    private String comment;

    @Column(name = "isApproved", nullable = false)
    private Boolean isApproved = true;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isApproved == null) {
            isApproved = true;
        }
    }

    // ========== GETTERS & SETTERS ==========
    
    public Integer getReviewID() { 
        return reviewID; 
    }
    
    public void setReviewID(Integer reviewID) { 
        this.reviewID = reviewID; 
    }

    public int getRating() { 
        return rating; 
    }
    
    public void setRating(int rating) { 
        this.rating = rating; 
    }

    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getComment() { 
        return comment; 
    }
    
    public void setComment(String comment) { 
        this.comment = comment; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public User getUser() { 
        return user; 
    }
    
    public void setUser(User user) { 
        this.user = user; 
    }

    public Product getProduct() { 
        return product; 
    }
    
    public void setProduct(Product product) { 
        this.product = product; 
    }

    // ========== isApproved METHODS (CHỈ GIỮ 1 BỘ) ==========
    
    /**
     * Getter chuẩn JavaBean cho Boolean field
     */
    public Boolean getIsApproved() { 
        return isApproved; 
    }
    
    /**
     * Setter chuẩn JavaBean cho Boolean field
     */
    public void setIsApproved(Boolean isApproved) { 
        this.isApproved = isApproved; 
    }

    /**
     * Getter kiểu boolean primitive (để tương thích với code cũ)
     * Trả về false nếu isApproved là null
     */
    public boolean isApproved() {
        return isApproved != null && isApproved;
    }
    
    /**
     * Setter kiểu boolean primitive (để tương thích với code cũ)
     */
    public void setApproved(boolean approved) {
        this.isApproved = approved;
    }
}