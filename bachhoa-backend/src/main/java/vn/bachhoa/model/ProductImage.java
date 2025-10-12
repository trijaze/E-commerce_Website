package vn.bachhoa.model;

import javax.persistence.*;

@Entity @Table(name="product_images")
public class ProductImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @ManyToOne(optional = false) @JoinColumn(name="product_id")
    private Product product;

    @Column(nullable=false, length=500) private String imageUrl;
    private boolean isMain;

    public Integer getImageId() { return imageId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isMain() { return isMain; }
    public void setMain(boolean main) { isMain = main; }
}
