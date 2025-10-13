package vn.bachhoa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.bachhoa.dto.ReviewDTO;
import vn.bachhoa.dto.request.CreateReviewRequest;
import vn.bachhoa.service.ReviewService;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/secure/reviews")
public class ReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ReviewService reviewService = new ReviewService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ======================= GET: Public ===========================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String productIdParam = req.getParameter("productId");
            if (productIdParam == null || productIdParam.isBlank()) {
                JsonUtil.writeJson(resp, Map.of("error", "Missing productId"), HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            int productId = Integer.parseInt(productIdParam);
            var reviews = reviewService.getReviewsForProduct(productId);
            JsonUtil.ok(resp, reviews);
            
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, Map.of("error", "productId must be a number"), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.writeJson(resp, Map.of("error", "Internal error"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // ======================= POST: Requires login ===========================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ✅ 1. Kiểm tra authentication
        Integer userId = (Integer) req.getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, 
                Map.of("error", "Bạn cần đăng nhập để gửi đánh giá."), 
                HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // ✅ 2. Validate productId
            String productIdParam = req.getParameter("productId");
            if (productIdParam == null || productIdParam.isBlank()) {
                JsonUtil.writeJson(resp, 
                    Map.of("error", "Thiếu tham số productId"), 
                    HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            int productId = Integer.parseInt(productIdParam);

            // ✅ 3. Parse request body
            CreateReviewRequest reqBody = objectMapper.readValue(req.getReader(), CreateReviewRequest.class);
            
            // ✅ 4. Validate request data
            if (reqBody.getRating() < 1 || reqBody.getRating() > 5) {
                JsonUtil.writeJson(resp, 
                    Map.of("error", "Rating phải từ 1 đến 5"), 
                    HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            if (reqBody.getComment() == null || reqBody.getComment().trim().isEmpty()) {
                JsonUtil.writeJson(resp, 
                    Map.of("error", "Nội dung đánh giá không được để trống"), 
                    HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // ✅ 5. Create DTO
            ReviewDTO dto = new ReviewDTO();
            dto.setRating(reqBody.getRating());
            dto.setTitle(reqBody.getTitle() != null ? reqBody.getTitle().trim() : null);
            dto.setComment(reqBody.getComment().trim());

            // ✅ 6. Save review
            ReviewDTO saved = reviewService.addReview(userId, productId, dto);
            JsonUtil.writeJson(resp, saved, HttpServletResponse.SC_CREATED);

        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, 
                Map.of("error", "productId phải là số"), 
                HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeJson(resp, 
                Map.of("error", e.getMessage()), 
                HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.writeJson(resp, 
                Map.of("error", "Lỗi khi xử lý yêu cầu: " + e.getMessage()), 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}