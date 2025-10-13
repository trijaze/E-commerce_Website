package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.dto.ProductDetailDTO;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/** ✅ Servlet xử lý API admin cho sản phẩm: /api/secure/admin/products */
@WebServlet(urlPatterns = {"/api/secure/admin/products", "/api/secure/admin/products/*"})
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
                writeError(resp, 400, "Missing required fields: name, price, categoryId");
                return;
            }
            
            // Create new product
            ProductDetailDTO product = mapToProductDTO(data);
            ProductDetailDTO created = productDAO.createProduct(product);
            
            if (created != null) {
                JsonUtil.ok(resp, wrap(created));
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
            
            // Update product
            ProductDetailDTO product = mapToProductDTO(data);
            product.setId(id); // Ensure ID is set
            
            boolean updated = productDAO.updateProduct(product);
            
            if (updated) {
                ProductDetailDTO updatedProduct = productDAO.findDetailDTOById(id);
                JsonUtil.ok(resp, wrap(updatedProduct));
            } else {
                writeError(resp, 404, "Product not found or update failed");
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

            boolean deleted = productDAO.deleteProduct(id);
            
            if (deleted) {
                JsonUtil.ok(resp, Map.of("message", "Product deleted successfully", "id", id));
            } else {
                writeError(resp, 404, "Product not found or delete failed");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            writeError(resp, 500, ex.getMessage());
        }
    }

    /** 🔹 Danh sách sản phẩm cho admin (bao gồm inactive) */
    private void handleAdminList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String catParam = req.getParameter("categoryId");
        String statusParam = req.getParameter("status");
        String searchParam = req.getParameter("search");
        
        // TODO: Implement filtered search for admin
        List<ProductDetailDTO> list = productDAO.findAllDetailDTO(); // Get all products including inactive
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
               data.containsKey("price") && 
               data.get("price") != null &&
               data.containsKey("categoryId") && 
               data.get("categoryId") != null;
    }

    /** 🔹 Helper: Map request data to ProductDetailDTO */
    private ProductDetailDTO mapToProductDTO(Map<String, Object> data) {
        ProductDetailDTO product = new ProductDetailDTO();
        
        if (data.containsKey("name")) {
            product.setName(((String) data.get("name")).trim());
        }
        if (data.containsKey("description")) {
            product.setDescription((String) data.get("description"));
        }
        if (data.containsKey("price")) {
            Object price = data.get("price");
            if (price instanceof Number) {
                product.setPrice(((Number) price).doubleValue());
            }
        }
        if (data.containsKey("stock")) {
            Object stock = data.get("stock");
            if (stock instanceof Number) {
                product.setStock(((Number) stock).intValue());
            } else {
                product.setStock(0); // Default stock
            }
        }
        if (data.containsKey("categoryId")) {
            Object categoryId = data.get("categoryId");
            if (categoryId instanceof Number) {
                product.setCategoryId(((Number) categoryId).intValue());
            }
        }
        if (data.containsKey("imageUrl")) {
            product.setImageUrl((String) data.get("imageUrl"));
        }
        if (data.containsKey("status")) {
            Object status = data.get("status");
            if (status instanceof Boolean) {
                product.setStatus((Boolean) status);
            } else {
                product.setStatus(true); // Default active
            }
        } else {
            product.setStatus(true); // Default active
        }
        
        return product;
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