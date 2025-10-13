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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");

        if (userId == null) {
            JsonUtil.writeJson(resp, Map.of("error", "Bạn cần đăng nhập để thực hiện chức năng này."), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String productIdParam = req.getParameter("productId");
            if (productIdParam == null || productIdParam.isBlank()) {
                throw new IllegalArgumentException("Query parameter 'productId' is required.");
            }
            int productId = Integer.parseInt(productIdParam);
            
            // Sửa lỗi 2: Dùng getInputStream()
            ReviewDTO reviewData = objectMapper.readValue(req.getInputStream(), ReviewDTO.class);
            ReviewDTO newReview = reviewService.addReview(userId, productId, reviewData);
            JsonUtil.writeJson(resp, newReview, HttpServletResponse.SC_CREATED);
            
        // Sửa lỗi 1: Bỏ NumberFormatException
        } catch (IllegalArgumentException e) { 
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalStateException e) {
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), HttpServletResponse.SC_FORBIDDEN);
        }
        catch (Exception e) {
            e.printStackTrace();
            JsonUtil.writeJson(resp, Map.of("error", "Yêu cầu không hợp lệ hoặc đã có lỗi xảy ra."), HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}