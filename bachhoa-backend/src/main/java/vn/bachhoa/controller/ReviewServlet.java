// src/main/java/vn/bachhoa/controller/ReviewServlet.java
package vn.bachhoa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.bachhoa.dto.ReviewDTO;
import vn.bachhoa.service.ReviewService;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter; // <-- IMPORT
import java.util.LinkedHashMap; // <-- IMPORT
import java.util.List; // <-- IMPORT
import java.util.Map; // <-- IMPORT
import java.util.stream.Collectors; // <-- IMPORT

@WebServlet("/api/secure/reviews")
public class ReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReviewService reviewService = new ReviewService();
    // ObjectMapper này vẫn cần thiết cho việc ĐỌC request body trong doPost
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Lấy danh sách review cho một sản phẩm.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // B1: Lấy danh sách review gốc từ service (vẫn chứa đối tượng LocalDateTime)
            int productId = getProductIdFromParameter(req);
            List<ReviewDTO> reviews = reviewService.getReviewsForProduct(productId);

            // B2: Tạo định dạng ngày tháng chuẩn ISO 8601 mà JavaScript hiểu được
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            // B3: Chuyển đổi List<ReviewDTO> thành List<Map<String, Object>>
            //     Trong quá trình này, ta sẽ "ép" LocalDateTime thành String.
            List<Map<String, Object>> reviewsForJson = reviews.stream().map(reviewDto -> {
                Map<String, Object> reviewMap = new LinkedHashMap<>();
                reviewMap.put("reviewId", reviewDto.getReviewId());
                reviewMap.put("rating", reviewDto.getRating());
                reviewMap.put("title", reviewDto.getTitle());
                reviewMap.put("comment", reviewDto.getComment());
                reviewMap.put("username", reviewDto.getUsername());
                
                // Chuyển LocalDateTime thành String
                if (reviewDto.getCreatedAt() != null) {
                    reviewMap.put("createdAt", formatter.format(reviewDto.getCreatedAt()));
                } else {
                    reviewMap.put("createdAt", null);
                }
                
                return reviewMap;
            }).collect(Collectors.toList());

            // B4: Gửi danh sách đã được định dạng an toàn cho JsonUtil
            // JsonUtil sẽ không gặp vấn đề gì vì không còn đối tượng LocalDateTime nào nữa.
            JsonUtil.ok(resp, reviewsForJson);

        } catch (IllegalArgumentException e) {
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            JsonUtil.writeJson(resp, Map.of("error", "An internal server error occurred."), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    // Phương thức doPost và getProductIdFromParameter giữ nguyên
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, Map.of("error", "Authentication required to post a review"), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        try {
            int productId = getProductIdFromParameter(req);
            // ObjectMapper vẫn dùng ở đây để đọc request, không ảnh hưởng gì
            ReviewDTO reviewData = objectMapper.readValue(req.getReader(), ReviewDTO.class);
            ReviewDTO newReview = reviewService.addReview(userId, productId, reviewData);
            JsonUtil.writeJson(resp, newReview, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            JsonUtil.writeJson(resp, Map.of("error", "Invalid request data or server error."), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private int getProductIdFromParameter(HttpServletRequest req) {
        String productIdParam = req.getParameter("productId");
        if (productIdParam == null || productIdParam.isBlank()) {
            throw new IllegalArgumentException("Query parameter 'productId' is required.");
        }
        try {
            return Integer.parseInt(productIdParam);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Query parameter 'productId' must be a valid number.");
        }
    }
}