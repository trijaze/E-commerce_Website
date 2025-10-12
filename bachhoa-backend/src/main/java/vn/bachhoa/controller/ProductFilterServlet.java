package vn.bachhoa.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;
import vn.nhom7.bachhoa.dao.ProductRepository;
import vn.nhom7.bachhoa.model.Product;
import vn.nhom7.bachhoa.dto.ProductDTO;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/products")
public class ProductFilterServlet extends HttpServlet {
    private final ProductRepository repo = new ProductRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // --- Lấy tham số filter ---
        String keyword = req.getParameter("q");
        Integer categoryId = parseInt(req.getParameter("categoryId"));
        Integer supplierId = parseInt(req.getParameter("supplierId"));
        String priceRange = req.getParameter("priceRange");
        Double minPrice = parseDouble(req.getParameter("minPrice"));
        Double maxPrice = parseDouble(req.getParameter("maxPrice"));
        String sort = req.getParameter("sort");

        // --- Gọi Repository ---
        List<Product> products = repo.filterProducts(
                keyword, categoryId, supplierId,
                priceRange, minPrice, maxPrice, sort
        );

        // --- Tạo Gson ---
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        // --- Map sang DTO ---
        List<ProductDTO> productDTOs = products.stream().map(p -> new ProductDTO(
                p.getProductId(),
                p.getName(),
                p.getDescription(),
                p.getBasePrice(),
                p.getCategory() != null ? p.getCategory().getCategoryName() : null,
                p.getSupplier() != null ? p.getSupplier().getName() : null,
                p.getImageUrls()
        )).collect(Collectors.toList());

        // --- Xuất JSON ---
        resp.setContentType("application/json; charset=UTF-8");
        gson.toJson(productDTOs, resp.getWriter());
    }

    private Integer parseInt(String value) {
        try { return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : null; }
        catch (NumberFormatException e) { return null; }
    }

    private Double parseDouble(String value) {
        try { return (value != null && !value.isEmpty()) ? Double.parseDouble(value) : null; }
        catch (NumberFormatException e) { return null; }
    }
}