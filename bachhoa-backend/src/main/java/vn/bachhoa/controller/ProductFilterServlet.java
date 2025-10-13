package vn.bachhoa.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;          // <— bỏ chữ Việt trong dòng import
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import vn.bachhoa.dao.ProductRepository;
import vn.bachhoa.dto.ProductDTO;
import vn.bachhoa.model.Product;

/**
 * Lưu ý: map sang /api/products/filter để KHÔNG đụng với ProductServlet (/api/products/*)
 */
@WebServlet("/api/products/filter")
public class ProductFilterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ProductRepository repo = new ProductRepository();
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // --- Lấy tham số filter ---
        String keywordRaw = req.getParameter("q");
        String keyword = (keywordRaw != null && !keywordRaw.trim().isEmpty()) ? keywordRaw.trim() : null;
        Integer categoryId = parseInt(req.getParameter("categoryId"));
        Integer supplierId = parseInt(req.getParameter("supplierId"));
        String priceRange  = req.getParameter("priceRange");
        Double minPrice    = parseDouble(req.getParameter("minPrice"));
        Double maxPrice    = parseDouble(req.getParameter("maxPrice"));
        String sort        = req.getParameter("sort");

        // --- Gọi Repository ---
        List<Product> products = repo.filterProducts(
                keyword, categoryId, supplierId, priceRange, minPrice, maxPrice, sort
        );

        // --- Map sang DTO (dùng constructor ProductDTO(Product) để khỏi gọi getter lẻ)
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());

        // --- Xuất JSON ---
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        gson.toJson(productDTOs, resp.getWriter());
    }

    private Integer parseInt(String value) {
        try { return (value != null && !value.isBlank()) ? Integer.parseInt(value) : null; }
        catch (NumberFormatException e) { return null; }
    }

    private Double parseDouble(String value) {
        try { return (value != null && !value.isBlank()) ? Double.parseDouble(value) : null; }
        catch (NumberFormatException e) { return null; }
    }
}
