package vn.bachhoa.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="cart_items")
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne(optional = false) @JoinColumn(name="cart_id")
    private Cart cart;

    @ManyToOne(optional = false) @JoinColumn(name="variant_id")
    private ProductVariant variant;

    private int quantity;
    @Column(precision=12, scale=2) private BigDecimal unitPrice;

    public Long getCartItemId() { return cartItemId; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
