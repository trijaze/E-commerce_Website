package vn.nhom7.bachhoa.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {
    private Integer productId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String categoryName;
    private String supplierName;
    private List<String> imageUrls;

    // --- Constructor ---
    public ProductDTO(Integer productId, String name, String description, BigDecimal basePrice,
                      String categoryName, String supplierName, List<String> imageUrls) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.categoryName = categoryName;
        this.supplierName = supplierName;
        this.imageUrls = imageUrls;
    }

    // --- Getters ---
    public Integer getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public String getCategoryName() { return categoryName; }
    public String getSupplierName() { return supplierName; }
    public List<String> getImageUrls() { return imageUrls; }
}
