package vn.bachhoa.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "productVariants")  // camelCase theo quy ước bạn, nếu DB đang camelCase
public class ProductVariant implements Serializable {
	private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer variantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId")  // camelCase
    private Product product;

    @Column(columnDefinition = "json")
    private String attributes; // JSON: {"size":"500g","color":"red"}

    @Column(precision = 13, scale = 2)
    private BigDecimal price;

    public ProductVariant() {}

    // Getters / Setters
    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Optional helper method
    public String getVariantName() {
        if (attributes == null || attributes.isEmpty()) return null;
        return attributes;
    }

    // equals / hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariant)) return false;
        ProductVariant other = (ProductVariant) o;
        return variantId != null && variantId.equals(other.getVariantId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(variantId);
    }
}
