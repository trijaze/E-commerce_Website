package vn.bachhoa.promotions;

import vn.bachhoa.common.Json;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * API endpoint: /api/promotions
 *
 * Methods supported:
 * GET  /api/promotions               -> list all promotions
 * GET  /api/promotions?code=CODE     -> get promotion by code (active only)
 * GET  /api/promotions?categoryId=ID -> get active promotions for category
 *
 * POST /api/promotions  (admin) -> create promotion (body: Promotion JSON)
 * PUT  /api/promotions  (admin) -> update promotion (body: Promotion JSON, must include id)
 * DELETE /api/promotions?id=ID   (admin) -> delete promotion
 *
 * NOTE: adjust isAdmin(req) according to your auth/session implementation.
 */
@WebServlet("/api/promotions")
public class PromotionServlet extends HttpServlet {

    private PromotionDAO dao;

    @Override
    public void init() {
        dao = new PromotionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String code = req.getParameter("code");
            String cat = req.getParameter("categoryId");
            resp.setContentType("application/json; charset=UTF-8");
            if (code != null && !code.isEmpty()) {
                Promotion p = dao.findByCode(code);
                Json.write(resp, p); // returns null if not found
                return;
            }
            if (cat != null && !cat.isEmpty()) {
                int categoryId = Integer.parseInt(cat);
                List<Promotion> list = dao.listActiveForCategory(categoryId);
                Json.write(resp, list);
                return;
            }
            List<Promotion> all = dao.listAll();
            Json.write(resp, all);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            Json.write(resp, new ErrorResult("server_error", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.setStatus(403);
            Json.write(resp, new ErrorResult("forbidden", "Admin only"));
            return;
        }
        try {
            // assume Json.read(req, Promotion.class) exists and maps date strings to LocalDateTime
            Promotion p = Json.read(req, Promotion.class);
            // basic validation
            if (p.getCode() == null || p.getStartAt() == null || p.getEndAt() == null) {
                resp.setStatus(400);
                Json.write(resp, new ErrorResult("bad_request", "code/startAt/endAt required"));
                return;
            }
            int id = dao.create(p);
            p.setId(id);
            resp.setStatus(201);
            Json.write(resp, p);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            Json.write(resp, new ErrorResult("server_error", e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.setStatus(403);
            Json.write(resp, new ErrorResult("forbidden", "Admin only"));
            return;
        }
        try {
            Promotion p = Json.read(req, Promotion.class);
            if (p.getId() == null) {
                resp.setStatus(400);
                Json.write(resp, new ErrorResult("bad_request", "id required"));
                return;
            }
            dao.update(p);
            Json.write(resp, p);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            Json.write(resp, new ErrorResult("server_error", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.setStatus(403);
            Json.write(resp, new ErrorResult("forbidden", "Admin only"));
            return;
        }
        try {
            String idS = req.getParameter("id");
            if (idS == null) {
                resp.setStatus(400);
                Json.write(resp, new ErrorResult("bad_request", "id required"));
                return;
            }
            int id = Integer.parseInt(idS);
            dao.delete(id);
            Json.write(resp, new SimpleResult("ok", "deleted"));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            Json.write(resp, new ErrorResult("server_error", e.getMessage()));
        }
    }

    // ---- helper classes for simple JSON responses ----
    public static class ErrorResult {
        public String error;
        public String message;
        public ErrorResult(String error, String message) { this.error = error; this.message = message; }
    }
    public static class SimpleResult {
        public String status;
        public String message;
        public SimpleResult(String status, String message) { this.status = status; this.message = message; }
    }

    /**
     * Replace this with your real admin check logic:
     * Example in your project: session attribute "userRole" = "ADMIN"
     */
    private boolean isAdmin(HttpServletRequest req) {
        HttpSession sess = req.getSession(false);
        if (sess == null) return false;
        // Option A: project stores role as "userRole" string
        Object roleObj = sess.getAttribute("userRole");
        if (roleObj != null && "ADMIN".equalsIgnoreCase(roleObj.toString())) return true;

        // Option B: project stores full user object under "user" session attribute
        Object userObj = sess.getAttribute("user");
        if (userObj != null) {
            // adapt: if your user object has getRole() method
            try {
                java.lang.reflect.Method m = userObj.getClass().getMethod("getRole");
                Object r = m.invoke(userObj);
                if (r != null && "ADMIN".equalsIgnoreCase(r.toString())) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }
}
