package products.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "productimages")
public class ProductImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "imageId")
  private Integer imageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "productId", nullable = false)
  private Product product;

  @Column(name = "imageUrl", length = 500, nullable = false)
  private String imageUrl;

  @Column(name = "isMain")
  private Boolean isMain;

  // getters
  public Integer getImageId() { return imageId; }
  public Product getProduct() { return product; }
  public String getImageUrl() { return imageUrl; }
  public Boolean getIsMain() { return isMain; }
}
