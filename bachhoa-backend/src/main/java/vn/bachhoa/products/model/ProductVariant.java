package vn.bachhoa.products.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productvariants")
public class ProductVariant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "variantId")
  private Integer variantId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "productId", nullable = false)
  private Product product;

  // DB là kiểu JSON nên để columnDefinition="json"
  @Column(name = "attributes", columnDefinition = "json")
  private String attributes; // lưu chuỗi JSON

  @Column(name = "price", precision = 12, scale = 2)
  private BigDecimal price;

  public Integer getVariantId() { return variantId; }
  public Product getProduct() { return product; }
  public String getAttributes() { return attributes; }
  public BigDecimal getPrice() { return price; }
}
