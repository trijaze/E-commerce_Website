package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.dto.ProductDTO;
import vn.bachhoa.dto.ProductDetailDTO;
import vn.bachhoa.util.JsonUtil;

//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/** ✅ Servlet xử lý API sản phẩm: /api/products, /api/products/{id}, /api/products/{id}/related */
//@WebServlet(urlPatterns = {"/api/products", "/api/products/*"})
public class ProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String path = Optional.ofNullable(req.getPathInfo()).orElse("").trim();

        try {
            if (path.isEmpty() || "/".equals(path)) {
                handleList(req, resp);
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
                handleDetail(id, resp);
            } else if (parts.length == 2 && "related".equalsIgnoreCase(parts[1])) {
                int limit = tryParseInt(req.getParameter("limit"), 8);
                handleRelated(id, limit, resp);
            } else {
                writeError(resp, 404, "Not found");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            writeError(resp, 500, ex.getMessage());
        }
    }

    /** 🔹 Danh sách sản phẩm hoặc theo categoryId */
    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String catParam = req.getParameter("categoryId");
        List<ProductDTO> list = (catParam != null && !catParam.isBlank())
                ? productDAO.findByCategoryDTO(Integer.parseInt(catParam))
                : productDAO.findAllDTO();
        JsonUtil.ok(resp, wrap(list));
    }

    /** 🔹 Chi tiết sản phẩm */
    private void handleDetail(Integer id, HttpServletResponse resp) throws IOException {
        ProductDetailDTO dto = productDAO.findDetailDTOById(id);
        if (dto == null) {
            writeError(resp, 404, "Product not found");
            return;
        }
        JsonUtil.ok(resp, wrap(dto));
    }

    /** 🔹 Sản phẩm liên quan */
    private void handleRelated(Integer id, int limit, HttpServletResponse resp) throws IOException {
        List<ProductDTO> list = productDAO.findRelatedDTO(id, limit);
        JsonUtil.ok(resp, wrap(list));
    }

    // Helpers
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

    private static int tryParseInt(String s, int def) {
        try { return (s == null || s.isBlank()) ? def : Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }
}
