package vn.bachhoa.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_products_category", columnList = "categoryId"),
        @Index(name = "idx_products_sku", columnList = "sku")
    },
    uniqueConstraints = @UniqueConstraint(name = "uk_products_sku", columnNames = {"sku"})
)
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "productId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    // KHÔNG đặt unique=true ở đây để tránh trùng với uniqueConstraints trên @Table
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // Nếu muốn dùng TEXT của MySQL: đổi thành @Column(name="description", columnDefinition="text")
    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "basePrice", precision = 13, scale = 2)
    private BigDecimal basePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", foreignKey = @ForeignKey(name = "fk_products_category"))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierId", foreignKey = @ForeignKey(name = "fk_products_supplier"))
    private Supplier supplier;

    // Tránh MultipleBagFetchException với 2 collection: dùng SUBSELECT + BatchSize
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 20)
    @OrderBy("variantId ASC")
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 20)
    @OrderBy("imageId ASC")
    private List<ProductImage> images = new ArrayList<>();

    // --- Constructors ---
    public Product() {}

    // --- Getters / Setters ---
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }

    // --- equals() / hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return productId != null && productId.equals(other.getProductId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId);
    }

    // --- tiện ích giữ quan hệ 2 chiều ---
    public void addVariant(ProductVariant v) {
        if (v == null) return;
        variants.add(v);
        v.setProduct(this);
    }
    public void removeVariant(ProductVariant v) {
        if (v == null) return;
        variants.remove(v);
        v.setProduct(null);
    }
    public void addImage(ProductImage img) {
        if (img == null) return;
        images.add(img);
        img.setProduct(this);
    }
    public void removeImage(ProductImage img) {
        if (img == null) return;
        images.remove(img);
        img.setProduct(null);
    }
}
