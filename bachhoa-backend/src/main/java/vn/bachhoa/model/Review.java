package vn.bachhoa.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="reviews")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(optional = false) @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(optional = false) @JoinColumn(name="product_id")
    private Product product;

    private short rating; // 1..5
    @Column(length=1000) private String title;
    @Column(length=4000) private String comment;
    private boolean isApproved;
    private LocalDateTime createdAt = LocalDateTime.now();
}
