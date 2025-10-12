package vn.nhom7.bachhoa.model;

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
    @Column(name = "product_id")
    @Expose private Integer productId;

    @Column(nullable = false)
    @Expose private String name;

    @Expose private String description;

    @Column(name = "base_price")
    @Expose private BigDecimal basePrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @Expose private Category category;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @Expose private Supplier supplier;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    // Getter/Setter
    public Integer getProductId() { return productId; }
    public void setProductId(Integer id) { this.productId = id; }

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

    //Trả danh sách URL ảnh
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
