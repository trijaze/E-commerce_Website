package vn.bachhoa.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="product_variants")
public class ProductVariant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer variantId;

    @ManyToOne(optional = false) @JoinColumn(name="product_id")
    private Product product;

    @Column(columnDefinition = "json")
    private String attributes; // JSON: e.g. {"size":"500g","color":"red"}

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    public Integer getVariantId() { return variantId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getAttributes() { return attributes; }
    public void setAttributes(String attributes) { this.attributes = attributes; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
