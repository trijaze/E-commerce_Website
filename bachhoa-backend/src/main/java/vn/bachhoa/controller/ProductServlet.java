package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.dto.ProductDetailDTO;
import vn.bachhoa.model.Product;
import vn.bachhoa.model.ProductImage;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ProductServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");

        // path: "/{id}" hoặc "/{id}/related"
        String path = Optional.ofNullable(req.getPathInfo()).orElse("").trim();
        if (path.isEmpty() || "/".equals(path)) {
            // Không implement list ở servlet này để tránh lệ thuộc DAO chưa có findAll()
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing product id");
            return;
        }

        String[] parts = Arrays.stream(path.split("/"))
                .filter(s -> s != null && !s.isBlank())
                .toArray(String[]::new);

        Integer id;
        try {
            id = Integer.parseInt(parts[0]);
        } catch (NumberFormatException ex) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product id");
            return;
        }

        try {
            if (parts.length == 1) {
                getDetail(id, resp);
                return;
            }
            if (parts.length == 2 && "related".equalsIgnoreCase(parts[1])) {
                int limit = parseInt(req.getParameter("limit"), 8);
                getRelated(id, limit, resp);
                return;
            }
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Not found");
        } catch (Exception ex) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private void getDetail(Integer id, HttpServletResponse resp) throws IOException {
        Product p = productDAO.findByIdWithRelations(id); // đã JOIN FETCH category/supplier/images/variants
        if (p == null) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }
        // Dùng đúng DTO constructor có sẵn, không tự build tay
        ProductDetailDTO dto = new ProductDetailDTO(p);
        JsonUtil.ok(resp, wrap(dto));
    }

    private void getRelated(Integer id, int limit, HttpServletResponse resp) throws IOException {
        var list = productDAO.findRelated(id, limit);

        var items = new ArrayList<Map<String, Object>>();
        for (Product p : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("productId", p.getProductId());
            m.put("name", p.getName());
            m.put("basePrice", p.getBasePrice());

            // Lấy ảnh đại diện: ưu tiên ảnh isMain = true, nếu không có thì lấy ảnh đầu tiên
            String url = null;
            if (p.getImages() != null && !p.getImages().isEmpty()) {
                ProductImage main = null;
                for (ProductImage i : p.getImages()) {
                    if (Boolean.TRUE.equals(i.getIsMain())) { main = i; break; } // ✅ dùng getIsMain()
                }
                url = (main != null) ? main.getImageUrl() : p.getImages().get(0).getImageUrl();
            }
            m.put("imageUrl", url);

            items.add(m);
        }
        JsonUtil.ok(resp, wrap(items));
    }

    private static Map<String, Object> wrap(Object data) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("data", data);
        return m;
    }

    private static void writeError(HttpServletResponse resp, int code, String msg) throws IOException {
        resp.setStatus(code);
        JsonUtil.ok(resp, Map.of("error", msg));
    }

    private static int parseInt(String s, int def) {
        try { return (s == null || s.isBlank()) ? def : Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }
}
