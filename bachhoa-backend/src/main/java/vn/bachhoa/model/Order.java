package vn.bachhoa.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable=false, unique = true, length = 30)
    private String orderNumber;

    @ManyToOne(optional = false) @JoinColumn(name="user_id")
    private User user;

    @Column(precision=12, scale=2) private BigDecimal subtotal;
    @Column(precision=12, scale=2) private BigDecimal totalAmount;

    @Column(length=30) private String status; // pending/confirmed/shipping/completed/cancelled/returned
    @Column(length=20) private String paymentMethod; // COD, VNPAY,...

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Long getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
