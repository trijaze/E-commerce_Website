package vn.bachhoa.controller;

import vn.bachhoa.dao.UserDAO;
import vn.bachhoa.dao.AuditLogDAO;
import vn.bachhoa.model.User;
import vn.bachhoa.model.AuditLog;
import vn.bachhoa.util.JWTUtil;
import vn.bachhoa.util.PasswordUtil;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.Key;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.GsonBuilder; 
import vn.bachhoa.util.LocalDateTimeAdapter; 
import java.time.LocalDateTime;

@WebServlet("/api/auth/login")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private static class LoginRequest {
        String identifier;
        String password;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BufferedReader reader = req.getReader();
        LoginRequest cred = gson.fromJson(reader, LoginRequest.class);

        if (cred == null || cred.identifier == null || cred.password == null || cred.identifier.trim().isEmpty() || cred.password.trim().isEmpty()) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        User u = userDAO.findByIdentifier(cred.identifier);
        
        if (u == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Tên đăng nhập hoặc số điện thoại không tồn tại.");
            return;
        }

        String hashedRequestPassword = PasswordUtil.hashPassword(cred.password);
        if (!u.getPasswordHash().equals(hashedRequestPassword)) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Mật khẩu không chính xác.");
            return;
        }

        try {
            // Ghi nhật ký hành động đăng nhập
            AuditLog log = new AuditLog(u.getUserId(), "USER_LOGIN", "Users", String.valueOf(u.getUserId()));
            auditLogDAO.save(log);

         // Tạo tokens
         String accessToken = JWTUtil.generateToken(u, JWTUtil.ACCESS_TOKEN_EXPIRATION_MS, JWTUtil.JWT_SECRET_KEY); // <-- Bỏ (Key)
         
         System.out.println("LOGIN KEY OBJECT ID: " + System.identityHashCode(JWTUtil.JWT_REFRESH_SECRET_KEY));
         String refreshToken = JWTUtil.generateToken(u, JWTUtil.REFRESH_TOKEN_EXPIRATION_MS, JWTUtil.JWT_REFRESH_SECRET_KEY); // <-- Bỏ (Key) nếu có
         System.out.println("LOGIN KEY: " + JWTUtil.JWT_SECRET_KEY);
            Map<String, String> responseData = Map.of("accessToken", accessToken, "refreshToken", refreshToken);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, responseData);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi tạo token hoặc ghi nhật ký.");
        }
    }
    
    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        sendJsonResponse(resp, statusCode, Map.of("error", message));
    }

    private void sendJsonResponse(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(data));
    }
}