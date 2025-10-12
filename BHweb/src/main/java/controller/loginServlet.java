package controller;

import dao.UserDAO;
import dao.AuditLogDAO;
import entity.User;
import entity.AuditLogs;
import util.JwtUtils;
import util.Utils;
import util.JwtConfig;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.Key;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/auth/login")
public class loginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final Gson gson = new Gson();

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

        String hashedRequestPassword = Utils.hashPassword(cred.password);
        if (!u.getPassword().equals(hashedRequestPassword)) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Mật khẩu không chính xác.");
            return;
        }

        try {
            // Ghi nhật ký hành động đăng nhập
            AuditLogs log = new AuditLogs(u.getId(), "USER_LOGIN", "Users", String.valueOf(u.getId()));
            auditLogDAO.save(log);

            // Tạo tokens
            String accessToken = JwtUtils.generateToken(u, JwtConfig.ACCESS_TOKEN_EXPIRATION_MS, (Key) JwtConfig.JWT_SECRET_KEY);
            String refreshToken = JwtUtils.generateToken(u, JwtConfig.REFRESH_TOKEN_EXPIRATION_MS, (Key) JwtConfig.JWT_REFRESH_SECRET_KEY);

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
