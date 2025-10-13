package vn.bachhoa.model;

import javax.persistence.*;
import com.google.gson.annotations.Expose;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "OrderItems")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;

    // Mối quan hệ nhiều OrderItem thuộc về 1 Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @Expose(serialize = false, deserialize = true)
    @JsonIgnore // ✅ chặn Jackson, tránh vòng lặp khi serialize
    private Order order;

    @Column(name = "product_id")
    @Expose
    private int productId;

    @Column(name = "quantity")
    @Expose
    private int quantity;

    @Column(name = "price")
    @Expose
    private double price;

    @Column(name = "status")
    @Expose
    private String status = "pending_payment";

    // ===== Constructors =====
    public OrderItem() {}

    // ===== Getters / Setters =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // 🧮 Tính thành tiền từng item (nếu FE cần hiển thị)
    @Transient
    public double getSubtotal() {
        return this.price * this.quantity;
    }
}
