package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.dto.ProductDTO;
import vn.bachhoa.dto.ProductDetailDTO;
import vn.bachhoa.model.Product;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.ServletException;
// KHÔNG dùng @WebServlet vì đã map trong web.xml (/api/products/*)
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        // /api/products/{id} -> chi tiết
        String path = req.getPathInfo();
        if (path != null && path.length() > 1) {
            if (!path.matches("^/\\d+$")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.ok(resp, new ErrorResponse("Invalid product id"));
                return;
            }
            int id = Integer.parseInt(path.substring(1));
            try {
                Product p = dao.findDetailById(id);
                if (p == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonUtil.ok(resp, new ErrorResponse("Product not found"));
                    return;
                }
                JsonUtil.ok(resp, new ProductDetailDTO(p));
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JsonUtil.ok(resp, new ErrorResponse(e.getMessage()));
            }
            return;
        }

        // /api/products[?categoryId=...] -> danh sách
        String categoryIdParam = req.getParameter("categoryId");
        try {
            List<Product> products = (categoryIdParam == null || categoryIdParam.isBlank())
                    ? dao.findAll()
                    : dao.findByCategoryId(Integer.parseInt(categoryIdParam));

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
