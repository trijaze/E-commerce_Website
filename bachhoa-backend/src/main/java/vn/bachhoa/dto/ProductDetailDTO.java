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

    // Thông tin cơ bản
    public Integer productId;
    public String sku;
    public String name;
    public String description;
    public BigDecimal basePrice;

    // Thông tin phân loại (đưa sẵn cả id + name để FE dùng luôn)
    public Integer categoryId;
    public String categoryName;
    public Integer supplierId;
    public String supplierName;

    // Ảnh & biến thể
    public List<ImageDTO> images;
    public List<VariantDTO> variants;

    // --- Nested DTOs ---
    public static class ImageDTO {
        public Integer imageId;
        public String imageUrl;
        public Boolean isMain;

        public ImageDTO(ProductImage i) {
            this.imageId = i.getImageId();      // chỉnh nếu là getId()
            this.imageUrl = i.getImageUrl();    // chỉnh nếu là getUrl()
            this.isMain = i.isMain();        // chỉnh nếu là getMain() / isMain()
        }
    }

    public static class VariantDTO {
        public Integer variantId;
        public String variantName;
        public BigDecimal price;
        public String attributes; // nếu bạn lưu JSON string, để FE parse

        public VariantDTO(ProductVariant v) {
            this.variantId = v.getVariantId();       // chỉnh nếu là getId()
            this.variantName = v.getVariantName();   // chỉnh nếu là getName()
            this.price = v.getPrice();
            this.attributes = v.getAttributes();     // chỉnh nếu field khác (e.g. getOptionsJson())
        }
    }

    // --- Mapper từ entity -> DTO ---
    public ProductDetailDTO(Product p) {
        this.productId = p.getProductId();       // chỉnh nếu là getId()
        this.sku = p.getSku();
        this.name = p.getName();
        this.description = p.getDescription();
        this.basePrice = p.getBasePrice();

        Category c = p.getCategory();
        this.categoryId = (c != null) ? c.getCategoryId() : null; // có thể là getId()
        this.categoryName = (c != null) ? c.getName() : null;     // có nơi dùng getCategoryName()

        Supplier s = p.getSupplier();
        this.supplierId = (s != null) ? s.getSupplierId() : null; // có thể là getId()
        this.supplierName = (s != null) ? s.getName() : null;

        this.images = (p.getImages() != null)
                ? p.getImages().stream().map(ImageDTO::new).collect(Collectors.toList())
                : List.of();

        this.variants = (p.getVariants() != null)
                ? p.getVariants().stream().map(VariantDTO::new).collect(Collectors.toList())
                : List.of();
    }
}
