package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.dto.ProductDetailDTO;
import vn.bachhoa.model.Product;
import vn.bachhoa.model.Category;
import vn.bachhoa.model.Supplier;
import vn.bachhoa.util.JsonUtil;
import vn.bachhoa.util.JPAUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/** ✅ Servlet xử lý API admin cho sản phẩm: /api/secure/admin/products */
public class AdminProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String path = Optional.ofNullable(req.getPathInfo()).orElse("").trim();

        try {
            if (path.isEmpty() || "/".equals(path)) {
                // Lấy tất cả sản phẩm cho admin
                handleAdminList(req, resp);
                return;
            }

            String[] parts = Arrays.stream(path.split("/"))
                    .filter(s -> s != null && !s.isBlank())
                    .toArray(String[]::new);

            Integer id = tryParseInt(parts[0]);
            if (id == null) {
                writeError(resp, 400, "Invalid product id");
                return;
            }

            if (parts.length == 1) {
                handleAdminDetail(id, resp);
            } else {
                writeError(resp, 404, "Not found");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            writeError(resp, 500, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        
        try {
            // Parse JSON from request body
            Map<String, Object> data = JsonUtil.parseJson(req);
            
            // Validate required fields
            if (!validateProductData(data)) {
                writeError(resp, 400, "Missing required fields: name, basePrice, categoryId, supplierId");
                return;
            }
            
            // Create new product entity
            Product product = mapToProduct(data);
            Product created = productDAO.createProduct(product);

            if (created != null) {
                ProductDetailDTO dto = productDAO.findDetailDTOById(created.getProductId());
                JsonUtil.ok(resp, wrap(dto));
            } else {
                writeError(resp, 400, "Failed to create product");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            writeError(resp, 500, ex.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        
        String path = Optional.ofNullable(req.getPathInfo()).orElse("").trim();
        
        try {
            if (path.isEmpty() || "/".equals(path)) {
                writeError(resp, 400, "Product ID required");
                return;
            }

            String[] parts = Arrays.stream(path.split("/"))
                    .filter(s -> s != null && !s.isBlank())
                    .toArray(String[]::new);

            Integer id = tryParseInt(parts[0]);
            if (id == null) {
                writeError(resp, 400, "Invalid product id");
                return;
            }

            // Parse JSON from request body
            Map<String, Object> data = JsonUtil.parseJson(req);
            
            // Update product using EntityManager transaction
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();

                // Lấy Product với eager loading category và supplier
                String jpql = "SELECT p FROM Product p " +
                             "LEFT JOIN FETCH p.category " +
                             "LEFT JOIN FETCH p.supplier " +
                             "WHERE p.productId = :id";
                Product existing = em.createQuery(jpql, Product.class)
                                   .setParameter("id", id)
                                   .getSingleResult();

                if (existing == null) {
                    em.getTransaction().rollback();
                    writeError(resp, 404, "Product not found");
                    return;
                }

                // Update fields from request data
                updateProductFromData(existing, data, em);

                // Save changes
                Product updated = em.merge(existing);
                em.getTransaction().commit();

                ProductDetailDTO dto = new ProductDetailDTO(updated);
                JsonUtil.ok(resp, wrap(dto));

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            } finally {
                em.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            writeError(resp, 500, ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        
        String path = Optional.ofNullable(req.getPathInfo()).orElse("").trim();
        
        try {
            if (path.isEmpty() || "/".equals(path)) {
                writeError(resp, 400, "Product ID required");
                return;
            }

            String[] parts = Arrays.stream(path.split("/"))
                    .filter(s -> s != null && !s.isBlank())
                    .toArray(String[]::new);

            Integer id = tryParseInt(parts[0]);
            if (id == null) {
                writeError(resp, 400, "Invalid product id");
                return;
            }

            // Check if product exists
            ProductDetailDTO existing = productDAO.findDetailDTOById(id);
            if (existing == null) {
                writeError(resp, 404, "Product not found");
                return;
            }

            productDAO.deleteProduct(id);
            JsonUtil.ok(resp, Map.of("message", "Product deleted successfully", "id", id));

        } catch (Exception ex) {
            ex.printStackTrace();
            writeError(resp, 500, ex.getMessage());
        }
    }

    /** 🔹 Danh sách sản phẩm cho admin (bao gồm inactive) */
    private void handleAdminList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Get all products including inactive
        List<ProductDetailDTO> list = productDAO.findAllDetailDTO();
        JsonUtil.ok(resp, wrap(list));
    }

    /** 🔹 Chi tiết sản phẩm cho admin */
    private void handleAdminDetail(Integer id, HttpServletResponse resp) throws IOException {
        ProductDetailDTO dto = productDAO.findDetailDTOById(id);
        if (dto == null) {
            writeError(resp, 404, "Product not found");
            return;
        }
        JsonUtil.ok(resp, wrap(dto));
    }

    /** 🔹 Validate product data */
    private boolean validateProductData(Map<String, Object> data) {
        return data.containsKey("name") && 
               data.get("name") != null && 
               !((String) data.get("name")).trim().isEmpty() &&
               data.containsKey("basePrice") &&
               data.get("basePrice") != null &&
               data.containsKey("categoryId") &&
               data.get("categoryId") != null &&
               data.containsKey("supplierId") &&
               data.get("supplierId") != null;
    }

    /** 🔹 Helper: Map request data to Product entity */
    private Product mapToProduct(Map<String, Object> data) {
        Product product = new Product();

        if (data.containsKey("name")) {
            product.setName(((String) data.get("name")).trim());
        }
        if (data.containsKey("description")) {
            product.setDescription((String) data.get("description"));
        }
        if (data.containsKey("sku")) {
            product.setSku((String) data.get("sku"));
        }
        if (data.containsKey("basePrice")) {
            Object price = data.get("basePrice");
            if (price instanceof Number) {
                product.setBasePrice(BigDecimal.valueOf(((Number) price).doubleValue()));
            }
        }
        if (data.containsKey("categoryId")) {
            Object categoryId = data.get("categoryId");
            if (categoryId instanceof Number) {
                Category category = new Category();
                category.setCategoryId(((Number) categoryId).intValue());
                product.setCategory(category);
            }
        }
        if (data.containsKey("supplierId")) {
            Object supplierId = data.get("supplierId");
            if (supplierId instanceof Number) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(((Number) supplierId).intValue());
                product.setSupplier(supplier);
            }
        }
        
        return product;
    }

    /** 🔹 Helper: Update existing product from request data */
    private void updateProductFromData(Product product, Map<String, Object> data, EntityManager em) {
        if (data.containsKey("name")) {
            product.setName(((String) data.get("name")).trim());
        }
        if (data.containsKey("description")) {
            product.setDescription((String) data.get("description"));
        }
        if (data.containsKey("sku")) {
            product.setSku((String) data.get("sku"));
        }
        if (data.containsKey("basePrice")) {
            Object price = data.get("basePrice");
            if (price instanceof Number) {
                product.setBasePrice(BigDecimal.valueOf(((Number) price).doubleValue()));
            }
        }
        if (data.containsKey("categoryId")) {
            Object categoryId = data.get("categoryId");
            if (categoryId instanceof Number) {
                Category category = em.find(Category.class, ((Number) categoryId).intValue());
                product.setCategory(category);
            }
        }
        if (data.containsKey("supplierId")) {
            Object supplierId = data.get("supplierId");
            if (supplierId instanceof Number) {
                Supplier supplier = em.find(Supplier.class, ((Number) supplierId).intValue());
                product.setSupplier(supplier);
            }
        }
    }

    // Helper methods
    private static Map<String, Object> wrap(Object data) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("data", data);
        return m;
    }

    private static void writeError(HttpServletResponse resp, int code, String msg) throws IOException {
        resp.setStatus(code);
        JsonUtil.ok(resp, Map.of("error", msg));
    }

    private static Integer tryParseInt(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.parseInt(s); }
        catch (Exception e) { return null; }
    }
}
