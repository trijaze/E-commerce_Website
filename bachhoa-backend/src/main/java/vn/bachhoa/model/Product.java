package vn.bachhoa.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_products_category", columnList = "categoryId")
    },
    uniqueConstraints = @UniqueConstraint(name = "uk_products_sku", columnNames = {"sku"})
)
public class Product  {

    // ---------- Cột chính ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productId")
    private Integer productId;

    // ---------- Thuộc tính cơ bản ----------
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "basePrice", precision = 13, scale = 2)
    private BigDecimal basePrice;

    // ---------- Liên kết danh mục và nhà cung cấp ----------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", foreignKey = @ForeignKey(name = "fk_products_category"))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierId", foreignKey = @ForeignKey(name = "fk_products_supplier"))
    private Supplier supplier;

    // ---------- Danh sách biến thể ----------
    @OneToMany(
        mappedBy = "product",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Fetch(FetchMode.SUBSELECT) // Tránh lỗi MultipleBagFetchException
    @BatchSize(size = 20) // Hibernate sẽ load tối đa 20 products một lần
    @OrderBy("variantId ASC") // Đảm bảo thứ tự khi hiển thị
    private List<ProductVariant> variants = new ArrayList<>();

    // ---------- Danh sách hình ảnh ----------
    @OneToMany(
        mappedBy = "product",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 20)
    @OrderBy("imageId ASC")
    private List<ProductImage> images = new ArrayList<>();

    // ---------- Constructors ----------
    public Product() {}

    // ---------- Getters / Setters ----------
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

   
    public void addVariant(ProductVariant v) {
        if (v != null) {
            variants.add(v);
            v.setProduct(this);
        }
    }

    public void removeVariant(ProductVariant v) {
        if (v != null) {
            variants.remove(v);
            v.setProduct(null);
        }
    }

    public void addImage(ProductImage img) {
        if (img != null) {
            images.add(img);
            img.setProduct(this);
        }
    }

    public void removeImage(ProductImage img) {
        if (img != null) {
            images.remove(img);
            img.setProduct(null);
        }
    }
}
