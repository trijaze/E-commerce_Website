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

@WebServlet("/api/secure/reviews")
public class ReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReviewService reviewService = new ReviewService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Lấy danh sách review cho một sản phẩm.
     * Endpoint: GET /api/reviews?productId={id}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int productId = getProductIdFromParameter(req);
            var reviews = reviewService.getReviewsForProduct(productId);
            JsonUtil.ok(resp, reviews);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            JsonUtil.writeJson(resp, Map.of("error", "An internal server error occurred."), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Thêm một review mới cho sản phẩm (yêu cầu đăng nhập).
     * Endpoint: POST /api/reviews?productId={id}
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");

        if (userId == null) {
            JsonUtil.writeJson(resp, Map.of("error", "Authentication required to post a review"), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int productId = getProductIdFromParameter(req);
            ReviewDTO reviewData = objectMapper.readValue(req.getReader(), ReviewDTO.class);
            ReviewDTO newReview = reviewService.addReview(userId, productId, reviewData);
            JsonUtil.writeJson(resp, newReview, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException e) {
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            JsonUtil.writeJson(resp, Map.of("error", "Invalid request data or server error."), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Helper: Lấy productId từ query parameter.
     */
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