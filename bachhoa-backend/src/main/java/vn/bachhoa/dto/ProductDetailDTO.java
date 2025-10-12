package vn.bachhoa.dto;

import vn.bachhoa.model.Product;
import vn.bachhoa.model.ProductImage;
import vn.bachhoa.model.ProductVariant;
import vn.bachhoa.model.Category;
import vn.bachhoa.model.Supplier;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDetailDTO {

    public Integer productId;
    public String sku;
    public String name;
    public String description;
    public BigDecimal basePrice;

    public Integer categoryId;
    public String categoryName;
    public Integer supplierId;
    public String supplierName;

    public List<ImageDTO> images;
    public List<VariantDTO> variants;

    public static class ImageDTO {
        public Integer imageId;
        public String imageUrl;
        public boolean isMain;
        public Integer variantId;

        public ImageDTO(ProductImage i) {
            this.imageId = i.getImageId();
            this.imageUrl = i.getImageUrl();
            this.isMain = Boolean.TRUE.equals(i.getIsMain());
            this.variantId = (i.getVariant() != null) ? i.getVariant().getVariantId() : null;
        }
    }

    public static class VariantDTO {
        public Integer variantId;
        public String sku;
        public String name;      // ví dụ: "500g", "1kg"
        public BigDecimal price;

        public VariantDTO(ProductVariant v) {
            this.variantId = v.getVariantId();
            this.sku = v.getSku();
            this.name = v.getVariantName();
            this.price = v.getPrice();
        }
    }

    public ProductDetailDTO(Product p) {
        this.productId = p.getProductId();
        this.sku = p.getSku();
        this.name = p.getName();
        this.description = p.getDescription();
        this.basePrice = p.getBasePrice();

        Category c = p.getCategory();
        this.categoryId = (c != null) ? c.getCategoryId() : null;
        this.categoryName = (c != null) ? c.getName() : null;

        Supplier s = p.getSupplier();
        this.supplierId = (s != null) ? s.getSupplierId() : null;
        this.supplierName = (s != null) ? s.getName() : null;

        this.images = (p.getImages() != null)
                ? p.getImages().stream().map(ImageDTO::new).collect(Collectors.toList())
                : List.of();

        this.variants = (p.getVariants() != null)
                ? p.getVariants().stream().map(VariantDTO::new).collect(Collectors.toList())
                : List.of();
    }
}
