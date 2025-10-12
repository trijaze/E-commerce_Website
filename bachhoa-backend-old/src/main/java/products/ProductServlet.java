package products;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Json;

/**
 * HOA: API sản phẩm (JPA)
 *  - GET /api/products?limit=&offset=&q=
 *  - GET /api/products/{id}
 *  - GET /api/products/{id}/related   (limit mặc định 8)
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/api/products/*"})
public class ProductServlet extends HttpServlet {

    private final ProductServiceJpa jpaService = new ProductServiceJpa();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // null, "/", "/{id}", "/{id}/related"
        if (path == null || "/".equals(path)) { list(req, resp); return; }

        String[] parts = path.substring(1).split("/");
        if (parts.length == 2 && "related".equals(parts[1])) { related(resp, parts[0]); return; }
        if (parts.length == 1) { detail(resp, parts[0]); return; }

        Json.write(resp, 404, Map.of("ok", false, "message", "Not found"));
    }

    // ================= LIST (JPA) =================
    private void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String q = req.getParameter("q");
        int limit  = clampInt(req.getParameter("limit"), 12, 1, 50);
        int offset = Math.max(0, parseInt(req.getParameter("offset"), 0));

        try {
            var items = jpaService.list(q, limit, offset);
            Json.write(resp, 200, Map.of(
                "ok", true,
                "data", items,
                "limit", limit,
                "offset", offset
            ));
        } catch (Exception e) {
            Json.write(resp, 500, Map.of("ok", false, "message", e.getMessage()));
        }
    }

    // ================= DETAIL (JPA) =================
    private void detail(HttpServletResponse resp, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            Map<String, Object> data = jpaService.getDetail(id);
            if (data == null) {
                Json.write(resp, 404, Map.of("ok", false, "message", "Not found"));
                return;
            }
            Json.write(resp, 200, Map.of("ok", true, "data", data));
        } catch (Exception e) {
            Json.write(resp, 500, Map.of("ok", false, "message", e.getMessage()));
        }
    }

    // ================= /{id}/related (JPA) =================
    private void related(HttpServletResponse resp, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            int limit = 8; // mặc định
            List<Map<String, Object>> data = jpaService.getRelated(id, limit);
            Json.write(resp, 200, Map.of("ok", true, "data", data));
        } catch (Exception e) {
            Json.write(resp, 500, Map.of("ok", false, "message", e.getMessage()));
        }
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
