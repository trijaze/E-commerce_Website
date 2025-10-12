package vn.bachhoa.controller;

import vn.bachhoa.dao.UserDAO;
import vn.bachhoa.dao.AuditLogDAO;
import vn.bachhoa.model.User;
import vn.bachhoa.model.AuditLog;
import vn.bachhoa.util.PasswordUtil;
import vn.bachhoa.util.JWTUtil;
import vn.bachhoa.util.EmailUtil;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.Key;
import java.util.Map;

@WebServlet("/api/auth/register")
public class registerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final Gson gson = new Gson();

    private static class RegisterRequest {
        String username;
        String password;
        String phoneNumber;
        String email;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BufferedReader reader = req.getReader();
        RegisterRequest cred = gson.fromJson(reader, RegisterRequest.class);

        if (cred == null || cred.username == null || cred.password == null || cred.phoneNumber == null || cred.email == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        // --- KIỂM TRA DỮ LIỆU TRÙNG LẶP VÀ GỬI LỖI CHI TIẾT ---
        // Kiểm tra username tồn tại
        if (userDAO.usernameExists(cred.username)) {
            sendError(resp, HttpServletResponse.SC_CONFLICT, "Tên đăng nhập đã được sử dụng.");
            return;
        }
        
        // Kiểm tra số điện thoại tồn tại
        if (userDAO.phoneExists(cred.phoneNumber)) {
            sendError(resp, HttpServletResponse.SC_CONFLICT, "Số điện thoại đã được đăng ký.");
            return;
        }
        
        // Kiểm tra email tồn tại
        if (userDAO.emailExists(cred.email)) {
            sendError(resp, HttpServletResponse.SC_CONFLICT, "Email này đã được sử dụng.");
            return;
        }

        User newUser = null;
        try {
            // --- Nếu tất cả kiểm tra đều qua, tiếp tục quá trình đăng ký ---
            String hashed = PasswordUtil.hashPassword(cred.password);
            newUser = new User(cred.username, hashed, cred.phoneNumber, cred.email);
            userDAO.save(newUser);
            
            if (newUser.getUserId() == 0) {
                throw new Exception("Lỗi nghiêm trọng: Không thể lưu người dùng vào CSDL.");
            }

            // Ghi nhật ký đăng ký
            AuditLog log = new AuditLog(newUser.getUserId(), "USER_REGISTER", "Users", String.valueOf(newUser.getUserId()));
            auditLogDAO.save(log);
            
            // Gửi email chào mừng
            EmailUtil.sendWelcomeEmail(newUser.getEmail(), newUser.getUsername());
            
            // Tạo token
            String accessToken = JWTUtil.generateToken(newUser, JWTUtil.ACCESS_TOKEN_EXPIRATION_MS, (Key) JWTUtil.JWT_SECRET_KEY);
            String refreshToken = JWTUtil.generateToken(newUser, JWTUtil.REFRESH_TOKEN_EXPIRATION_MS, (Key) JWTUtil.JWT_REFRESH_SECRET_KEY);

            Map<String, String> responseData = Map.of("accessToken", accessToken, "refreshToken", refreshToken);
            sendJsonResponse(resp, HttpServletResponse.SC_CREATED, responseData);

        } catch (Exception e) {
            if (newUser != null && newUser.getUserId() > 0) {
                userDAO.delete(newUser.getUserId());
            }
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không mong muốn trong quá trình đăng ký.");
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