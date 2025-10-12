package vn.bachhoa.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(nullable=false, unique = true, length = 64) private String sku;
    @Column(nullable=false, length=200) private String name;
    @Column(length=1000) private String description;
    @Column(precision = 12, scale = 2) private BigDecimal basePrice;

    @ManyToOne @JoinColumn(name="category_id")
    private Category category;

    @ManyToOne @JoinColumn(name="supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    public Integer getProductId() { return productId; }
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
}
