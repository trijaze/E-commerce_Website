package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.model.Product;
import vn.bachhoa.dto.ProductDTO;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {

    private final ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json; charset=UTF-8");

        String categoryIdParam = req.getParameter("categoryId");

        try {
            List<Product> products;
            if (categoryIdParam == null) {
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
