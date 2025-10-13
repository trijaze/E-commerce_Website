package vn.bachhoa.filter;

import vn.bachhoa.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter này chạy SAU CorsFilter, chỉ cho các endpoint cần được bảo vệ.
 * Nhiệm vụ duy nhất của nó là xác thực JWT.
 */
@WebFilter("/api/secure/*")
public class JwtFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String authHeader = req.getHeader("Authorization");
        System.out.println("Authorization header: " + authHeader);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Authorization header is missing or invalid\"}");
            return;
        }

        String token = authHeader.substring(7);
        
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                     .setSigningKey(JWTUtil.JWT_SECRET_KEY)
                                     .build()
                                     .parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Integer userId = claims.get("userId", Integer.class);
            String username = claims.getSubject();

            req.setAttribute("userId", userId);
            req.setAttribute("username", username);
            
            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Token has expired\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Invalid token\"}");
        }
    }
}