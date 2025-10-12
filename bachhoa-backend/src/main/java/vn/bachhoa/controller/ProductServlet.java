package vn.bachhoa.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.model.Product;
import vn.bachhoa.model.ProductImage;
import vn.bachhoa.model.ProductVariant;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ProductServlet extends HttpServlet {
    private final ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // /api/products or /api/products/{id}
        String path = Optional.ofNullable(req.getPathInfo()).orElse("/");
        if ("/".equals(path)) {
            int page = parseInt(req.getParameter("page"), 1);
            int size = parseInt(req.getParameter("size"), 12);
            String q = req.getParameter("q");
            Integer categoryId = req.getParameter("categoryId") != null ? Integer.parseInt(req.getParameter("categoryId")) : null;
            var list = dao.findAll(page, size, q, categoryId);
            var dto = list.stream().map(ProductDTO::from).collect(Collectors.toList());
            JsonUtil.ok(resp, Map.of("items", dto, "page", page, "size", size));
        } else {
            try {
                int id = Integer.parseInt(path.substring(1));
                Product p = dao.findById(id);
                if (p == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonUtil.ok(resp, Map.of("error", "Product not found"));
                    return;
                }
                JsonUtil.ok(resp, ProductDetailDTO.from(p));
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.ok(resp, Map.of("error", "Invalid product id"));
            }
        }
    }

    private int parseInt(String s, int def) {
        try { return s == null ? def : Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    // --- DTOs: avoid recursion on JSON ---
    static class ProductDTO {
        public int productId;
        public String sku;
        public String name;
        public String description;
        public BigDecimal basePrice;
        public String mainImage;

        static ProductDTO from(Product p) {
            ProductDTO d = new ProductDTO();
            d.productId = p.getProductId();
            d.sku = p.getSku();
            d.name = p.getName();
            d.description = p.getDescription();
            d.basePrice = p.getBasePrice();
            d.mainImage = Optional.ofNullable(p.getImages())
                    .orElse(Collections.emptyList()).stream()
                    .filter(ProductImage::isMain)
                    .map(ProductImage::getImageUrl)
                    .findFirst().orElse(null);
            return d;
        }
    }

    static class VariantDTO {
        public int variantId;
        public String attributes; // raw JSON string
        public BigDecimal price;

        static VariantDTO from(ProductVariant v) {
            VariantDTO d = new VariantDTO();
            d.variantId = v.getVariantId();
            d.attributes = v.getAttributes();
            d.price = v.getPrice();
            return d;
        }
    }

    static class ProductDetailDTO extends ProductDTO {
        public List<String> images;
        public List<VariantDTO> variants;

        static ProductDetailDTO from(Product p) {
            ProductDetailDTO d = new ProductDetailDTO();
            ProductDTO base = ProductDTO.from(p);
            d.productId = base.productId;
            d.sku = base.sku;
            d.name = base.name;
            d.description = base.description;
            d.basePrice = base.basePrice;
            d.mainImage = base.mainImage;

            d.images = Optional.ofNullable(p.getImages()).orElse(Collections.emptyList())
                    .stream().map(ProductImage::getImageUrl).collect(Collectors.toList());

            d.variants = Optional.ofNullable(p.getVariants()).orElse(Collections.emptyList())
                    .stream().map(VariantDTO::from).collect(Collectors.toList());
            return d;
        }
    }
}
