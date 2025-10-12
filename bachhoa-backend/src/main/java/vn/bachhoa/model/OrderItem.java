package vn.bachhoa.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(optional = false) @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne(optional = false) @JoinColumn(name="variant_id")
    private ProductVariant variant;

    @Column(length=200) private String productName;
    @Column(precision=12, scale=2) private BigDecimal unitPrice;
    private int quantity;

    public Long getOrderItemId() { return orderItemId; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}
