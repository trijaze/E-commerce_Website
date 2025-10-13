package vn.bachhoa.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productvariants")
public class ProductVariant  {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variantId")
    private Integer variantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", foreignKey = @ForeignKey(name = "fk_variants_product"))
    private Product product;

    // SKU của biến thể
    @Column(name = "variantSku", length = 64, nullable = false)
    private String variantSku;

    // Thuộc tính mô tả (ví dụ: "500g", "1kg", "vị tôm cay" ...)
    @Column(name = "attributes", length = 255)
    private String attributes;

    // Giá bán của biến thể
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    // Số lượng tồn kho
    @Column(name = "stockQuantity")
    private Integer stockQuantity;

    // --- Getters / Setters ---
    public Integer getVariantId() { return variantId; }
    public void setVariantId(Integer variantId) { this.variantId = variantId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getVariantSku() { return variantSku; }
    public void setVariantSku(String variantSku) { this.variantSku = variantSku; }

    public String getAttributes() { return attributes; }
    public void setAttributes(String attributes) { this.attributes = attributes; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

}
