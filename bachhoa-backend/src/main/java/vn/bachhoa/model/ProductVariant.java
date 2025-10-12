package vn.bachhoa.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "productvariants")
public class ProductVariant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variantId")
    private Integer variantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", foreignKey = @ForeignKey(name = "fk_variants_product"))
    private Product product;

    @Column(name = "sku", length = 64)
    private String sku;

    @Column(name = "variantName", length = 255)
    private String variantName;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    // --- getters/setters ---
    public Integer getVariantId() { return variantId; }
    public void setVariantId(Integer variantId) { this.variantId = variantId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariant)) return false;
        ProductVariant other = (ProductVariant) o;
        return variantId != null && variantId.equals(other.getVariantId());
    }
    @Override public int hashCode() { return Objects.hashCode(variantId); }
}
