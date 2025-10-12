package com.hoa.model;

import javax.persistence.*;

// Đánh dấu đây là một entity JPA (bảng trong DB)
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Mặc định column name sẽ lấy tên biến (productName)
    // nhưng ta vẫn có thể khai báo rõ để tránh lỗi mapping
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;

    // === Constructors ===
    public CartItem() {}

    public CartItem(String productName, double price, int quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // === Getters & Setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // === Optional: giúp in ra dễ debug ===
    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
