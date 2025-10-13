package vn.bachhoa.filter;

import vn.bachhoa.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Filter này chạy SAU JwtFilter, chỉ cho các endpoint của admin.
 * Nhiệm vụ duy nhất của nó là kiểm tra vai trò (role) của người dùng.
 */
// Chỉ chạy cho các API adminJwtFilter đã xác thực token.
@WebFilter("/api/secure/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Kiểm tra vai trò từ token đó.
        String authHeader = req.getHeader("Authorization");
        
        // Kiểm tra lại, đảm bảo filter có thể hoạt động độc lập nếu cần.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Authentication token is required.\"}");
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Chúng ta cần parse lại token ở đây để lấy ra 'role'.
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                        .setSigningKey(JWTUtil.JWT_SECRET_KEY)
                                        .build()
                                        .parseClaimsJws(token);
            
            Claims claims = claimsJws.getBody();

            // KIỂM TRA QUYỀN ADMIN
            String role = claims.get("role", String.class);
            if (!Objects.equals(role, "admin")) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\":\"Access denied. Admin role required.\"}");
                return;
            }

            // Nếu đúng là admin, cho phép request đi tiếp.
            chain.doFilter(request, response);

        } catch (Exception e) {
            // Bắt các lỗi token (hết hạn, không hợp lệ) mà có thể JwtFilter đã bỏ qua (dù hiếm)
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Invalid or expired token.\"}");
        }
    }
}

