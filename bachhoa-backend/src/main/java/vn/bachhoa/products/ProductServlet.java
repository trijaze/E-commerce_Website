package vn.bachhoa.products;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.bachhoa.common.Db;
import vn.bachhoa.common.Json;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * HOA: API sản phẩm
 *  - GET /api/products?limit=&offset=&q=
 *  - GET /api/products/{id}
 *  - GET /api/products/{id}/related   (limit mặc định 8)
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/api/products/*"})
public class ProductServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // null, "/", "/{id}", "/{id}/related"
        if (path == null || "/".equals(path)) { list(req, resp); return; }

        String[] parts = path.substring(1).split("/");
        // /{id}/related
        if (parts.length == 2 && "related".equals(parts[1])) { related(resp, parts[0]); return; }
        // /{id}
        if (parts.length == 1) { detail(resp, parts[0]); return; }

        Json.write(resp, 404, Map.of("ok", false, "message", "Not found"));
    }

    // ================= LIST =================
    private void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String q = req.getParameter("q");
        int limit  = clampInt(req.getParameter("limit"), 12, 1, 50);
        int offset = Math.max(0, parseInt(req.getParameter("offset"), 0));

        String base =
            "SELECT p.productId, p.name, p.description, p.basePrice, " +
            "  (SELECT imageUrl FROM productimages i " +
            "   WHERE i.productId = p.productId AND i.isMain = 1 LIMIT 1) AS imageUrl, " +
            "  COALESCE((SELECT MIN(v.price) FROM productvariants v WHERE v.productId = p.productId), p.basePrice) AS minPrice " +
            "FROM products p ";
        String where = (q != null && !q.isBlank()) ? "WHERE p.name LIKE ? " : "";
        String tail  = "ORDER BY p.productId DESC LIMIT ? OFFSET ?";

        try (Connection cn = Db.get().getConnection();
             PreparedStatement st = cn.prepareStatement(base + where + tail)) {

            int idx = 1;
            if (q != null && !q.isBlank()) st.setString(idx++, "%" + q + "%");
            st.setInt(idx++, limit);
            st.setInt(idx, offset);

            List<Map<String, Object>> items = new ArrayList<>();
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("productId", rs.getInt("productId"));
                    row.put("name", rs.getString("name"));
                    row.put("description", rs.getString("description"));
                    row.put("basePrice", rs.getBigDecimal("basePrice"));
                    row.put("minPrice", rs.getBigDecimal("minPrice"));
                    row.put("imageUrl", rs.getString("imageUrl"));
                    items.add(row);
                }
            }

            Json.write(resp, 200, Map.of(
                "ok", true,
                "data", items,
                "limit", limit,
                "offset", offset
            ));
        } catch (SQLException e) {
            Json.write(resp, 500, Map.of("ok", false, "message", e.getMessage()));
        }
    }

    // ================= DETAIL =================
    private void detail(HttpServletResponse resp, String idStr) throws IOException {
        try (Connection cn = Db.get().getConnection()) {
            int id = Integer.parseInt(idStr);

            Map<String, Object> prod = null;
            // product
            try (PreparedStatement st = cn.prepareStatement(
                    "SELECT productId, name, description, basePrice FROM products WHERE productId = ?")) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        prod = new LinkedHashMap<>();
                        prod.put("productId", rs.getInt("productId"));
                        prod.put("name", rs.getString("name"));
                        prod.put("description", rs.getString("description"));
                        prod.put("basePrice", rs.getBigDecimal("basePrice"));
                    }
                }
            }
            if (prod == null) {
                Json.write(resp, 404, Map.of("ok", false, "message", "Not found"));
                return;
            }

            // images (ảnh chính trước)
            try (PreparedStatement st = cn.prepareStatement(
                    "SELECT imageId, imageUrl, isMain FROM productimages " +
                    "WHERE productId = ? ORDER BY isMain DESC, imageId ASC")) {
                st.setInt(1, id);
                List<Map<String, Object>> imgs = new ArrayList<>();
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        imgs.add(Map.of(
                            "imageId", rs.getInt("imageId"),
                            "imageUrl", rs.getString("imageUrl"),
                            "isMain", rs.getBoolean("isMain")
                        ));
                    }
                }
                prod.put("images", imgs);
            }

            // variants (parse attributes JSON)
            BigDecimal min = (BigDecimal) prod.get("basePrice");
            List<Map<String, Object>> vars = new ArrayList<>();
            try (PreparedStatement st = cn.prepareStatement(
                    "SELECT variantId, attributes, price FROM productvariants " +
                    "WHERE productId = ? ORDER BY variantId ASC")) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        String attrsJson = rs.getString("attributes");
                        Map<String, Object> attrs;
                        try {
                            attrs = MAPPER.readValue(attrsJson, new TypeReference<Map<String, Object>>() {});
                        } catch (Exception ignore) {
                            attrs = Map.of();
                        }

                        BigDecimal price = rs.getBigDecimal("price");
                        if (price != null && (min == null || price.compareTo(min) < 0)) {
                            min = price;
                        }

                        Map<String, Object> v = new LinkedHashMap<>();
                        v.put("variantId", rs.getInt("variantId"));
                        v.put("attributes", attrs);
                        v.put("price", price);
                        vars.add(v);
                    }
                }
            }
            prod.put("variants", vars);
            prod.put("minPrice", min);

            // related (danh sách ngắn)
            prod.put("related", fetchRelated(cn, id, 8));

            Json.write(resp, 200, Map.of("ok", true, "data", prod));
        } catch (Exception e) {
            Json.write(resp, 500, Map.of("ok", false, "message", e.getMessage()));
        }
    }

    // ================= /{id}/related =================
    private void related(HttpServletResponse resp, String idStr) throws IOException {
        try (Connection cn = Db.get().getConnection()) {
            int id = Integer.parseInt(idStr);
            int limit = 8; // mặc định
            List<Map<String, Object>> data = fetchRelated(cn, id, limit);
            Json.write(resp, 200, Map.of("ok", true, "data", data));
        } catch (Exception e) {
            Json.write(resp, 500, Map.of("ok", false, "message", e.getMessage()));
        }
    }

    /**
     * Lấy danh sách sản phẩm liên quan:
     *  - Ưu tiên cùng category với sản phẩm hiện tại (loại trừ chính nó)
     *  - Nếu trống thì fallback sang các sản phẩm khác (trừ chính nó)
     *  Trả kèm imageUrl & minPrice để FE hiển thị card.
     */
    private List<Map<String, Object>> fetchRelated(Connection cn, int exceptId, int limit) throws SQLException {
        List<Map<String, Object>> out = new ArrayList<>();

        String sameCategorySql =
            "SELECT p.productId, p.name, " +
            "  (SELECT imageUrl FROM productimages i WHERE i.productId=p.productId AND i.isMain=1 LIMIT 1) AS imageUrl, " +
            "  COALESCE((SELECT MIN(v.price) FROM productvariants v WHERE v.productId=p.productId), p.basePrice) AS minPrice " +
            "FROM products p " +
            "WHERE p.categoryId = (SELECT categoryId FROM products WHERE productId = ?) " +
            "  AND p.productId <> ? " +
            "ORDER BY p.productId DESC LIMIT ?";

        try (PreparedStatement st = cn.prepareStatement(sameCategorySql)) {
            st.setInt(1, exceptId);
            st.setInt(2, exceptId);
            st.setInt(3, Math.max(1, Math.min(24, limit)));
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    out.add(Map.of(
                        "productId", rs.getInt("productId"),
                        "name", rs.getString("name"),
                        "imageUrl", rs.getString("imageUrl"),
                        "minPrice", rs.getBigDecimal("minPrice")
                    ));
                }
            }
        }

        // fallback nếu chưa đủ
        if (out.isEmpty()) {
            String fallbackSql =
                "SELECT p.productId, p.name, " +
                "  (SELECT imageUrl FROM productimages i WHERE i.productId=p.productId AND i.isMain=1 LIMIT 1) AS imageUrl, " +
                "  COALESCE((SELECT MIN(v.price) FROM productvariants v WHERE v.productId=p.productId), p.basePrice) AS minPrice " +
                "FROM products p WHERE p.productId <> ? " +
                "ORDER BY p.productId DESC LIMIT ?";

            try (PreparedStatement st = cn.prepareStatement(fallbackSql)) {
                st.setInt(1, exceptId);
                st.setInt(2, Math.max(1, Math.min(24, limit)));
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        out.add(Map.of(
                            "productId", rs.getInt("productId"),
                            "name", rs.getString("name"),
                            "imageUrl", rs.getString("imageUrl"),
                            "minPrice", rs.getBigDecimal("minPrice")
                        ));
                    }
                }
            }
        }

        return out;
    }

    // ===== utils =====
    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    private int clampInt(String s, int def, int min, int max) {
        int v = parseInt(s, def);
        return Math.max(min, Math.min(max, v));
    }
}
