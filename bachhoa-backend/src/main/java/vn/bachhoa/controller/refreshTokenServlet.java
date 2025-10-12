package vn.bachhoa.controller;

import vn.bachhoa.model.User;
import vn.bachhoa.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.Key;
import java.util.Map;

/**
 * Servlet này xử lý việc làm mới (refresh) Access Token.
 * Đã được tái cấu trúc và tích hợp chức năng ghi nhật ký.
 */
@WebServlet("/api/auth/refresh")
public class refreshTokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();
    private static class RefreshRequest {
        String refreshToken;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        RefreshRequest refreshRequest = parseRefreshRequest(req);
        if (refreshRequest == null || refreshRequest.refreshToken == null || refreshRequest.refreshToken.trim().isEmpty()) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Refresh token không được để trống.");
            return;
        }

        try {
            Claims claims = validateAndGetClaims(refreshRequest.refreshToken);

            Integer userId = claims.get("userId", Integer.class);
            String role = claims.get("role", String.class);
            String username = claims.getSubject();

            if (userId == null || role == null || username == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Refresh token không hợp lệ: thiếu thông tin cần thiết.");
                return;
            }

            // Tạo một đối tượng User tạm thời để truyền vào hàm generateToken
            User userFromToken = new User();
            userFromToken.setUserId(userId);
            userFromToken.setUsername(username);
            userFromToken.setRole(role);

            String newAccessToken = JWTUtil.generateToken(
                userFromToken,
                JWTUtil.ACCESS_TOKEN_EXPIRATION_MS,
                (Key) JWTUtil.JWT_SECRET_KEY
            );

            sendJsonResponse(resp, HttpServletResponse.SC_OK, Map.of("accessToken", newAccessToken));

        } catch (ExpiredJwtException e) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Refresh token đã hết hạn. Vui lòng đăng nhập lại.");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Refresh token không hợp lệ.");
        }
    }

    private RefreshRequest parseRefreshRequest(HttpServletRequest req) {
        try (BufferedReader reader = req.getReader()) {
            return gson.fromJson(reader, RefreshRequest.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Claims validateAndGetClaims(String token) throws Exception {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                                    .setSigningKey(JWTUtil.JWT_REFRESH_SECRET_KEY)
                                    .build()
                                    .parseClaimsJws(token);
        return claimsJws.getBody();
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        sendJsonResponse(resp, statusCode, Map.of("error", message));
    }

    private void sendJsonResponse(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(data));
    }
}

