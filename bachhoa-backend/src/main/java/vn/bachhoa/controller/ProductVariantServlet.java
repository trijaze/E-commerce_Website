package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductVariantDAO;
import vn.bachhoa.model.ProductVariant;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet quản lý ProductVariant
 * Endpoints:
 *  GET    /api/products/{productId}/variants   -> Lấy tất cả variants của product
 *  POST   /api/products/{productId}/variants   -> Tạo variant mới cho product
 *  GET    /api/variants/{variantId}            -> Lấy chi tiết 1 variant
 *  PUT    /api/variants/{variantId}            -> Cập nhật variant
 *  DELETE /api/variants/{variantId}            -> Xóa variant
 */
public class ProductVariantServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    private final ProductVariantDAO dao = new ProductVariantDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        
        String servletPath = req.getServletPath(); // /api/products hoặc /api/variants
        String pathInfo = req.getPathInfo();       // /{id}/variants hoặc /{id}
        
        try {
            if ("/api/products".equals(servletPath)) {
                // GET /api/products/{productId}/variants
                if (pathInfo != null && pathInfo.matches("^/\\d+/variants$")) {
                    String[] parts = pathInfo.split("/");
                    int productId = Integer.parseInt(parts[1]);
                    
                    List<ProductVariant> variants = dao.findByProductId(productId);
                    JsonUtil.ok(resp, variants);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.ok(resp, new ErrorResponse("Invalid URL format"));
                }
                
            } else if ("/api/variants".equals(servletPath)) {
                // GET /api/variants/{variantId}
                if (pathInfo != null && pathInfo.matches("^/\\d+$")) {
                    int variantId = Integer.parseInt(pathInfo.substring(1));
                    
                    ProductVariant variant = dao.findVariantById(variantId);
                    if (variant == null) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        JsonUtil.ok(resp, new ErrorResponse("Variant not found"));
                    } else {
                        JsonUtil.ok(resp, variant);
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.ok(resp, new ErrorResponse("Invalid variant ID"));
                }
            }
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Invalid ID format"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.ok(resp, new ErrorResponse("Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        
        String pathInfo = req.getPathInfo(); // /{productId}/variants
        
        if (pathInfo == null || !pathInfo.matches("^/\\d+/variants$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Invalid URL format. Use: /api/products/{productId}/variants"));
            return;
        }
        
        try {
            String[] parts = pathInfo.split("/");
            int productId = Integer.parseInt(parts[1]);
            
            // Đọc dữ liệu variant từ request body
            String jsonStr = req.getReader().lines()
                    .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
            
            ProductVariant variant = JsonUtil.fromJson(jsonStr, ProductVariant.class);
            
            // Validate dữ liệu cơ bản
            if (variant.getPrice() == null || variant.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.ok(resp, new ErrorResponse("Valid price is required"));
                return;
            }
            
            ProductVariant created = dao.createVariant(variant, productId);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.ok(resp, created);
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Invalid product ID format"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.ok(resp, new ErrorResponse("Create variant failed: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        
        String pathInfo = req.getPathInfo(); // /{variantId}
        
        if (pathInfo == null || !pathInfo.matches("^/\\d+$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Variant ID is required"));
            return;
        }
        
        try {
            int variantId = Integer.parseInt(pathInfo.substring(1));
            
            // Kiểm tra variant có tồn tại
            ProductVariant existing = dao.findVariantById(variantId);
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.ok(resp, new ErrorResponse("Variant not found"));
                return;
            }
            
            // Đọc dữ liệu update
            String jsonStr = req.getReader().lines()
                    .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
            
            ProductVariant updateData = JsonUtil.fromJson(jsonStr, ProductVariant.class);
            updateData.setVariantId(variantId);
            updateData.setProduct(existing.getProduct()); // Giữ nguyên product relationship
            
            ProductVariant updated = dao.updateVariant(updateData);
            JsonUtil.ok(resp, updated);
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Invalid variant ID format"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.ok(resp, new ErrorResponse("Update variant failed: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        
        String pathInfo = req.getPathInfo(); // /{variantId}
        
        if (pathInfo == null || !pathInfo.matches("^/\\d+$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Variant ID is required"));
            return;
        }
        
        try {
            int variantId = Integer.parseInt(pathInfo.substring(1));
            
            // Kiểm tra variant có tồn tại
            ProductVariant existing = dao.findVariantById(variantId);
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.ok(resp, new ErrorResponse("Variant not found"));
                return;
            }
            
            dao.deleteVariant(variantId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Invalid variant ID format"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.ok(resp, new ErrorResponse("Delete variant failed: " + e.getMessage()));
        }
    }
    
    static class ErrorResponse {
        public String error;
        ErrorResponse(String msg) { this.error = msg; }
    }
}