package vn.bachhoa.products;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.bachhoa.jpa.JPAUtil;
import vn.bachhoa.products.model.Product;
import vn.bachhoa.products.model.ProductImage;
import vn.bachhoa.products.model.ProductVariant;

public class ProductServiceJpa {
  private static final ObjectMapper M = new ObjectMapper();

  // ===== LIST =====
  public List<Map<String,Object>> list(String q, int limit, int offset) {
    EntityManager em = JPAUtil.EMF.createEntityManager();
    try {
      String jpql = "select p from Product p "
                  + (q != null && !q.isBlank() ? "where p.name like :kw " : "")
                  + "order by p.productId desc";
      TypedQuery<Product> query = em.createQuery(jpql, Product.class);
      if (q != null && !q.isBlank()) query.setParameter("kw", "%" + q + "%");
      query.setFirstResult(Math.max(0, offset));
      query.setMaxResults(Math.max(1, Math.min(50, limit)));

      List<Product> products = query.getResultList();
      List<Map<String,Object>> items = new ArrayList<>();

      for (Product p : products) {
        // ảnh chính (hoặc ảnh đầu nếu không gắn isMain)
        String imageUrl = em.createQuery(
              "select i.imageUrl from ProductImage i " +
              "where i.product.productId = :pid and (i.isMain = true) " +
              "order by i.imageId asc", String.class)
            .setParameter("pid", p.getProductId())
            .setMaxResults(1)
            .getResultList().stream().findFirst().orElse(null);
        if (imageUrl == null) {
          imageUrl = em.createQuery(
              "select i.imageUrl from ProductImage i " +
              "where i.product.productId = :pid order by i.imageId asc", String.class)
            .setParameter("pid", p.getProductId())
            .setMaxResults(1).getResultList().stream().findFirst().orElse(null);
        }

        BigDecimal minVariant = em.createQuery(
            "select min(v.price) from ProductVariant v where v.product.productId = :pid",
            BigDecimal.class).setParameter("pid", p.getProductId()).getSingleResult();
        BigDecimal minPrice = (minVariant != null) ? minVariant : p.getBasePrice();

        Map<String,Object> row = new LinkedHashMap<>();
        row.put("productId", p.getProductId());
        row.put("name", p.getName());
        row.put("description", p.getDescription());
        row.put("basePrice", p.getBasePrice());
        row.put("minPrice", minPrice);
        row.put("imageUrl", imageUrl);
        items.add(row);
      }
      return items;
    } finally { em.close(); }
  }

  // ===== DETAIL (ĐÃ SỬA – tách truy vấn, không JOIN FETCH 2 bag) =====
  public Map<String,Object> getDetail(int id) {
    EntityManager em = JPAUtil.EMF.createEntityManager();
    try {
      // 1) Lấy product chính
      Product p = em.find(Product.class, id);
      if (p == null) return null;

      // 2) Lấy images (query riêng)
      List<ProductImage> imageList = em.createQuery(
          "select i from ProductImage i " +
          "where i.product.productId = :pid " +
          "order by i.isMain desc, i.imageId asc", ProductImage.class)
        .setParameter("pid", id)
        .getResultList();

      // 3) Lấy variants (query riêng)
      List<ProductVariant> variantList = em.createQuery(
          "select v from ProductVariant v " +
          "where v.product.productId = :pid " +
          "order by v.variantId asc", ProductVariant.class)
        .setParameter("pid", id)
        .getResultList();

      // 4) Build DTO
      Map<String,Object> prod = new LinkedHashMap<>();
      prod.put("productId", p.getProductId());
      prod.put("name", p.getName());
      prod.put("description", p.getDescription());
      prod.put("basePrice", p.getBasePrice());

      var imgs = Optional.ofNullable(imageList).orElse(List.of()).stream()
        .map(i -> Map.of(
            "imageId", i.getImageId(),
            "imageUrl", i.getImageUrl(),
            "isMain",  i.getIsMain()
        ))
        .collect(Collectors.toList());
      prod.put("images", imgs);

      BigDecimal min = p.getBasePrice();
      List<Map<String,Object>> vars = new ArrayList<>();
      for (ProductVariant v : Optional.ofNullable(variantList).orElse(List.of())) {
        Map<String,Object> attrs;
        try {
          attrs = M.readValue(
              Optional.ofNullable(v.getAttributes()).orElse("{}"),
              new TypeReference<Map<String,Object>>(){}
          );
        } catch (Exception ignore) { attrs = Map.of(); }

        if (v.getPrice()!=null && (min==null || v.getPrice().compareTo(min)<0)) {
          min = v.getPrice();
        }

        Map<String,Object> item = new LinkedHashMap<>();
        item.put("variantId", v.getVariantId());
        item.put("attributes", attrs);
        item.put("price", v.getPrice());
        vars.add(item);
      }
      prod.put("variants", vars);
      prod.put("minPrice", min);

      // 5) Related (giữ join fetch 1 bag ở hàm dưới là OK)
      prod.put("related", relatedInternal(em, p.getProductId(), p.getCategoryId(), 8));
      return prod;
    } finally { em.close(); }
  }

  // ===== RELATED =====
  public List<Map<String,Object>> getRelated(int productId, int limit) {
    EntityManager em = JPAUtil.EMF.createEntityManager();
    try {
      Integer cat = em.createQuery(
        "select p.categoryId from Product p where p.productId = :id", Integer.class)
        .setParameter("id", productId).getSingleResult();
      return relatedInternal(em, productId, cat, limit);
    } finally { em.close(); }
  }

  private List<Map<String,Object>> relatedInternal(EntityManager em, int exceptId, Integer catId, int limit) {
    if (catId == null) catId = -1;
    List<Product> ps = em.createQuery(
      "select distinct p from Product p left join fetch p.images " +
      " where p.categoryId = :cat and p.productId <> :id order by p.productId desc", Product.class)
      .setParameter("cat", catId).setParameter("id", exceptId)
      .setMaxResults(Math.max(1, Math.min(24, limit))).getResultList();

    List<Map<String,Object>> out = new ArrayList<>();
    for (Product p : ps) {
      String imageUrl = null;
      if (p.getImages()!=null && !p.getImages().isEmpty()) {
        imageUrl = p.getImages().stream()
          .sorted(Comparator.comparing((ProductImage i) -> Boolean.TRUE.equals(i.getIsMain())).reversed()
                   .thenComparing(ProductImage::getImageId))
          .map(ProductImage::getImageUrl).findFirst().orElse(null);
      }
      BigDecimal minVariant = em.createQuery(
        "select min(v.price) from ProductVariant v where v.product.productId = :pid", BigDecimal.class)
        .setParameter("pid", p.getProductId())
        .getSingleResult();
      BigDecimal minPrice = (minVariant != null) ? minVariant : p.getBasePrice();

      out.add(Map.of("productId", p.getProductId(), "name", p.getName(),
                     "imageUrl", imageUrl, "minPrice", minPrice));
    }
    return out;
  }
}
