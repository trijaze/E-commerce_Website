package vn.bachhoa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.bachhoa.dto.UserProfileDTO;
import vn.bachhoa.service.UserProfileService;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

// Endpoint này sẽ được bảo vệ bởi JwtFilter do có path là "/api/secure/*"
@WebServlet("/api/secure/user/profile")
public class UserProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final UserProfileService profileService = new UserProfileService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Xử lý yêu cầu GET để lấy thông tin hồ sơ người dùng hiện tại.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");

        if (userId == null) {
            JsonUtil.writeJson(resp, Map.of("error", "Unauthorized: User ID not found in token"), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserProfileDTO profile = profileService.getProfile(userId);

        if (profile == null) {
            JsonUtil.writeJson(resp, Map.of("error", "User profile not found"), HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        JsonUtil.ok(resp, profile);
    }

    /**
     * Xử lý yêu cầu PUT để cập nhật thông tin hồ sơ người dùng.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, Map.of("error", "Unauthorized: User ID not found in token"), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        try {
            // Đọc dữ liệu JSON từ body của request và chuyển thành UserProfileDTO
            UserProfileDTO profileUpdateData = objectMapper.readValue(req.getReader(), UserProfileDTO.class);
            
            // Gọi service để cập nhật
            UserProfileDTO updatedProfile = profileService.updateProfile(userId, profileUpdateData);
            
            // Trả về profile đã được cập nhật
            JsonUtil.ok(resp, updatedProfile);
            
        } catch (Exception e) {
            JsonUtil.writeJson(resp, Map.of("error", "Invalid request data or failed to update profile"), HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}