package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.model.Product;
import vn.bachhoa.dto.ProductDTO;
import vn.bachhoa.dto.ProductDetailDTO;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/products/*") // đổi sang /* để đọc pathInfo cho /{id}
public class ProductServlet extends HttpServlet {

    private final ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // CORS + JSON
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        // Nếu có pathInfo dạng "/123" thì trả chi tiết
        String path = req.getPathInfo(); // ví dụ "/123" hoặc null
        if (path != null && path.length() > 1) {
            if (!path.matches("^/\\d+$")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.ok(resp, new ErrorResponse("Invalid product id"));
                return;
            }
            int id = Integer.parseInt(path.substring(1));
            try {
                Product p = dao.findDetailById(id); // bạn đã thêm trong ProductDAO
                if (p == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonUtil.ok(resp, new ErrorResponse("Product not found"));
                    return;
                }
                JsonUtil.ok(resp, new ProductDetailDTO(p)); // trả DTO chi tiết (images + variants)
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JsonUtil.ok(resp, new ErrorResponse(e.getMessage()));
            }
            return;
        }

        // Không có pathInfo => danh sách (tuỳ chọn lọc theo categoryId)
        String categoryIdParam = req.getParameter("categoryId");
        try {
            List<Product> products;
            if (categoryIdParam == null || categoryIdParam.isBlank()) {
                products = dao.findAll();
            } else {
                int categoryId = Integer.parseInt(categoryIdParam);
                products = dao.findByCategoryId(categoryId);
            }

            List<ProductDTO> dtoList = products.stream()
                    .map(ProductDTO::new)
                    .collect(Collectors.toList());

            JsonUtil.ok(resp, dtoList);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Invalid categoryId format"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.ok(resp, new ErrorResponse(e.getMessage()));
        }
    }

    static class ErrorResponse {
        public String error;
        ErrorResponse(String msg) { this.error = msg; }
    }
}
