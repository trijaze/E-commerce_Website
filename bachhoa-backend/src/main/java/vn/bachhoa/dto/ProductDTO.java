package vn.bachhoa.dto;

import vn.bachhoa.model.Product;
import vn.bachhoa.model.ProductImage;
import vn.bachhoa.model.ProductVariant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDTO {
    private Integer productId;
    private String sku;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String categoryName;
    private String supplierName;
    private List<String> imageUrls;
    private List<String> variantNames;

    public ProductDTO(Product p) {
        if (p == null) return;

        this.productId = p.getProductId();
        this.sku = p.getSku();
        this.name = p.getName();
        this.description = p.getDescription();
        this.basePrice = p.getBasePrice();

        this.categoryName = p.getCategory() != null ? p.getCategory().getName() : null;
        this.supplierName = p.getSupplier() != null ? p.getSupplier().getName() : null;

        this.imageUrls = (p.getImages() != null && !p.getImages().isEmpty()) ?
                p.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                : new ArrayList<>();

        this.variantNames = (p.getVariants() != null && !p.getVariants().isEmpty()) ?
                p.getVariants().stream().map(ProductVariant::getVariantName).collect(Collectors.toList())
                : new ArrayList<>();
    }

    // Getters
    public Integer getProductId() { return productId; }
    public String getSku() { return sku; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public String getCategoryName() { return categoryName; }
    public String getSupplierName() { return supplierName; }
    public List<String> getImageUrls() { return imageUrls; }

    public List<String> getVariantNames() { return variantNames; }

}
