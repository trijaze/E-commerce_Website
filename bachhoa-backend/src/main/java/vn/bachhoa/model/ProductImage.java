package vn.bachhoa.model;

import javax.persistence.*;

@Entity
@Table(name = "productimages")
public class ProductImage  {
    

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", foreignKey = @ForeignKey(name = "fk_images_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variantId", foreignKey = @ForeignKey(name = "fk_images_variant"))
    private ProductVariant variant;

    @Column(length = 255)
    private String imageUrl;

    @Column
    private Boolean isMain;

    // --- Getters / Setters ---
    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsMain() { return isMain; }
    public void setIsMain(Boolean isMain) { this.isMain = isMain; }

}
