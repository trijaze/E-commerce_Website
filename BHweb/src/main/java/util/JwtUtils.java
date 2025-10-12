package util;

import entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key; // ✅ SỬA LỖI: Sử dụng kiểu 'Key' tổng quát hơn
import java.util.Date;

/**
 * Lớp tiện ích tập trung các chức năng liên quan đến JSON Web Tokens (JWT).
 */
public class JwtUtils {

    /**
     * Phương thức chung để tạo một JWT (Access Token hoặc Refresh Token).
     * @param user Đối tượng người dùng chứa thông tin để đưa vào token.
     * @param expirationMs Thời gian hết hạn của token (tính bằng mili giây).
     * @param secretKey Khóa bí mật để ký token (đã được sửa thành kiểu Key).
     * @return Một chuỗi JWT đã được ký.
     */
    public static String generateToken(User user, long expirationMs, Key secretKey) {
        long nowMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(nowMillis + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}

