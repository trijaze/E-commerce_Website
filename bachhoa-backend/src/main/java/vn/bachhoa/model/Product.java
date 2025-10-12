package vn.bachhoa.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productId")
    @Expose
    private Integer productId;

    @Column(nullable = false, length = 200)
    @Expose
    private String name;

    @Column(columnDefinition = "TEXT")
    @Expose
    private String description;

    @Column(name = "basePrice", precision = 12, scale = 2)
    @Expose
    private BigDecimal basePrice;

    // Quan hệ với Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    @Expose
    private Category category;

    // Quan hệ với Supplier
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierId")
    @Expose
    private Supplier supplier;

    // Quan hệ 1-n với ProductImage
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    // Quan hệ 1-n với ProductVariant
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariant> variants = new ArrayList<>();

    // ===== Getter/Setter =====
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

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

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }

    // ===== Hàm tiện ích: Trả danh sách URL ảnh =====
    @Transient
    public List<String> getImageUrls() {
        List<String> urls = new ArrayList<>();
        if (images != null) {
            for (ProductImage img : images) {
                urls.add(img.getImageUrl());
            }
        }
        return urls;
    }
}