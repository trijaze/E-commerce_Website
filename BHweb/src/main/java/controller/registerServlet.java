package controller;

import dao.UserDAO;
import dao.AuditLogDAO;
import entity.User;
import entity.AuditLogs;
import util.Utils;
import util.JwtConfig;
import util.JwtUtils;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.Key;
import java.util.Map;

@WebServlet("/api/auth/register")
public class registerServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final Gson gson = new Gson();

    private static class RegisterRequest {
        String username;
        String password;
        String phone;
        String email;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BufferedReader reader = req.getReader();
        RegisterRequest cred = gson.fromJson(reader, RegisterRequest.class);

        // ✅ LOG: In ra dữ liệu nhận được để kiểm tra
        System.out.println(">>> [registerServlet] Received data: " + gson.toJson(cred));

        if (cred == null || cred.username == null || cred.password == null || cred.phone == null || cred.email == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (userDAO.usernameExists(cred.username)) { /* ... */ }
        if (userDAO.phoneExists(cred.phone)) { /* ... */ }
        if (userDAO.emailExists(cred.email)) {
            sendError(resp, HttpServletResponse.SC_CONFLICT, "Email đã tồn tại");
            return;
        }

        User newUser = null;
        try {
            String hashed = Utils.hashPassword(cred.password);
            newUser = new User(cred.username, hashed, cred.phone, cred.email);

            // ✅ LOG CHẨN ĐOÁN QUAN TRỌNG
            System.out.println(">>> [registerServlet] User ID BEFORE save: " + newUser.getId());

            userDAO.save(newUser);

            // ✅ LOG CHẨN ĐOÁN QUAN TRỌNG NHẤT
            System.out.println(">>> [registerServlet] User ID AFTER save: " + newUser.getId());
            
            // Nếu ID sau khi lưu vẫn là 0, token sẽ bị "khuyết tật"
            if (newUser.getId() == 0) {
                throw new Exception("Failed to save user to database, ID was not generated.");
            }

            // Ghi nhật ký đăng ký
            AuditLogs log = new AuditLogs(newUser.getId(), "USER_REGISTER", "Users", String.valueOf(newUser.getId()));
            auditLogDAO.save(log);
            
            // Tạo token
            String accessToken = JwtUtils.generateToken(newUser, JwtConfig.ACCESS_TOKEN_EXPIRATION_MS, (Key) JwtConfig.JWT_SECRET_KEY);
            String refreshToken = JwtUtils.generateToken(newUser, JwtConfig.REFRESH_TOKEN_EXPIRATION_MS, (Key) JwtConfig.JWT_REFRESH_SECRET_KEY);

            Map<String, String> responseData = Map.of("accessToken", accessToken, "refreshToken", refreshToken);
            sendJsonResponse(resp, HttpServletResponse.SC_CREATED, responseData);

        } catch (Exception e) {
            if (newUser != null && newUser.getId() > 0) {
                userDAO.delete(newUser.getId());
            }
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không mong muốn trong quá trình đăng ký.");
        }
    }

    
    /**
     * Phương thức helper để gửi response lỗi dưới dạng JSON.
     * @param resp HttpServletResponse
     * @param statusCode Mã trạng thái HTTP (ví dụ: 401, 400).
     * @param message Thông điệp lỗi.
     */
    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        sendJsonResponse(resp, statusCode, Map.of("error", message));
    }

    /**
     * Phương thức helper chung để gửi response JSON.
     * @param resp HttpServletResponse
     * @param statusCode Mã trạng thái HTTP.
     * @param data Dữ liệu cần gửi (sẽ được chuyển thành JSON).
     */
    private void sendJsonResponse(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(data));
    }
}

