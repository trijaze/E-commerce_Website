package products.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "productId")
  private Integer productId;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "basePrice", precision = 12, scale = 2)
  private BigDecimal basePrice;

  @Column(name = "categoryId")
  private Integer categoryId;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<ProductImage> images;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<ProductVariant> variants;

  // getters
  public Integer getProductId() { return productId; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public BigDecimal getBasePrice() { return basePrice; }
  public Integer getCategoryId() { return categoryId; }
  public List<ProductImage> getImages() { return images; }
  public List<ProductVariant> getVariants() { return variants; }
}
