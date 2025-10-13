// src/main/java/vn/bachhoa/controller/PublicReviewServlet.java
package vn.bachhoa.controller;

import vn.bachhoa.dto.ReviewDTO;
import vn.bachhoa.service.ReviewService;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;


// ✅ QUAN TRỌNG: Đặt ở đường dẫn công khai, không có "/secure"
@WebServlet("/api/reviews")
public class PublicReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReviewService reviewService = new ReviewService();

    /**
     * Lấy danh sách review cho một sản phẩm (công khai cho tất cả mọi người).
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String productIdParam = req.getParameter("productId");
            if (productIdParam == null || productIdParam.isBlank()) {
                throw new IllegalArgumentException("Query parameter 'productId' is required.");
            }
            
            int productId = Integer.parseInt(productIdParam);
            List<ReviewDTO> reviews = reviewService.getReviewsForProduct(productId);
            
            // Xử lý lại ngày tháng để đảm bảo định dạng đúng, tránh lỗi Invalid Date
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            List<Map<String, Object>> reviewsForJson = reviews.stream().map(reviewDto -> {
                Map<String, Object> reviewMap = new LinkedHashMap<>();
                reviewMap.put("reviewId", reviewDto.getReviewId());
                reviewMap.put("rating", reviewDto.getRating());
                reviewMap.put("title", reviewDto.getTitle());
                reviewMap.put("comment", reviewDto.getComment());
                reviewMap.put("username", reviewDto.getUsername());
                if (reviewDto.getCreatedAt() != null) {
                    reviewMap.put("createdAt", formatter.format(reviewDto.getCreatedAt()));
                } else {
                    reviewMap.put("createdAt", null);
                }
                return reviewMap;
            }).collect(Collectors.toList());

            JsonUtil.ok(resp, reviewsForJson);

        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, Map.of("error", "Query parameter 'productId' must be a valid number."), HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
            JsonUtil.writeJson(resp, Map.of("error", "An internal server error occurred."), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}