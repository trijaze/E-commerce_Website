package vn.bachhoa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import vn.bachhoa.dto.UserProfileDTO;
import vn.bachhoa.dto.request.UpdateProfileRequest;
import vn.bachhoa.service.UserProfileService;
import vn.bachhoa.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/secure/users/profile")
public class UserProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserProfileService profileService = new UserProfileService();
    private final ObjectMapper objectMapper;

    public UserProfileServlet() {
        this.objectMapper = new ObjectMapper();
        // Đăng ký module để xử lý LocalDateTime
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");
        
        if (userId == null) {
            JsonUtil.writeJson(resp, Map.of("error", "Unauthorized: User ID not found in token"), 
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserProfileDTO profile = profileService.getProfile(userId);
        
        if (profile == null) {
            JsonUtil.writeJson(resp, Map.of("error", "User profile not found"), 
                    HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        JsonUtil.ok(resp, profile);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");
        
        if (userId == null) {
            JsonUtil.writeJson(resp, Map.of("error", "Unauthorized: User ID not found in token"), 
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // ✅ SỬA LỖI: Dùng UpdateProfileRequest thay vì UserProfileDTO
            UpdateProfileRequest updateRequest = objectMapper.readValue(
                    req.getInputStream(), 
                    UpdateProfileRequest.class
            );

            // Gọi service với request DTO
            UserProfileDTO updatedProfile = profileService.updateProfile(userId, updateRequest);
            
            JsonUtil.ok(resp, updatedProfile);

        } catch (IllegalArgumentException e) {
            // Lỗi validation (username đã tồn tại, v.v.)
            JsonUtil.writeJson(resp, Map.of("error", e.getMessage()), 
                    HttpServletResponse.SC_CONFLICT);
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            // Lỗi parse JSON
            e.printStackTrace();
            JsonUtil.writeJson(resp, Map.of("error", "Invalid JSON format: " + e.getMessage()), 
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            // Các lỗi khác
            e.printStackTrace();
            JsonUtil.writeJson(resp, Map.of("error", "Failed to update profile: " + e.getMessage()), 
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}