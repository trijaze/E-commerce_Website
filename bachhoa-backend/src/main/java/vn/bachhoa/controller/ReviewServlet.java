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
import java.util.Map;

// GET có thể công khai, POST cần xác thực
@WebServlet(urlPatterns = {"/api/products/*/reviews", "/api/secure/products/*/reviews"})
public class ReviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final ReviewService reviewService = new ReviewService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Lấy danh sách review cho một sản phẩm.
     * Endpoint: GET /api/products/{productId}/reviews
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int productId = getProductIdFromPath(req);
            var reviews = reviewService.getReviewsForProduct(productId);
            JsonUtil.ok(resp, reviews);
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, Map.of("error", "Invalid product ID"), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Thêm một review mới cho sản phẩm (yêu cầu đăng nhập).
     * Endpoint: POST /api/secure/products/{productId}/reviews
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, Map.of("error", "Authentication required to post a review"), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int productId = getProductIdFromPath(req);
            ReviewDTO reviewData = objectMapper.readValue(req.getReader(), ReviewDTO.class);
            
            ReviewDTO newReview = reviewService.addReview(userId, productId, reviewData);
            
            // Trả về 201 Created cùng với review vừa tạo
            JsonUtil.writeJson(resp, newReview, HttpServletResponse.SC_CREATED);
            
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, Map.of("error", "Invalid product ID"), HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            JsonUtil.writeJson(resp, Map.of("error", "Invalid request data"), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private int getProductIdFromPath(HttpServletRequest req) {
        String pathInfo = req.getPathInfo(); // Sẽ là "/{productId}/reviews"
        String[] pathParts = pathInfo.split("/");
        // pathParts[0] là chuỗi rỗng, pathParts[1] là productId
        return Integer.parseInt(pathParts[1]);
    }
}