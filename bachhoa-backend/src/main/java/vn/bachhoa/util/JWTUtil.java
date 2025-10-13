package vn.bachhoa.util;

import vn.bachhoa.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Lớp tiện ích để xử lý các hoạt động liên quan đến JSON Web Token (JWT).
 * Bao gồm tạo token, xác thực và trích xuất thông tin từ token.
 */
public final class JWTUtil {

    public static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000;

    public static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L;
    private static final String ACCESS_TOKEN_SECRET_STRING = "bachhoaonline1234567890securesecretkey";
    private static final String REFRESH_TOKEN_SECRET_STRING = "bachhoaonlinerefresh1234567890securekey";

    public static final Key JWT_SECRET_KEY = Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    public static final Key JWT_REFRESH_SECRET_KEY = Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    private JWTUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateToken(User user, long expirationMs, Key secretKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole());
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiryDate = new Date(nowMillis + expirationMs);

        return Jwts.builder()
                .setClaims(claims) // Đặt các custom claims
                .setSubject(user.getUsername()) // Đặt chủ thể của token (thường là username)
                .setIssuedAt(now) // Thời gian phát hành
                .setExpiration(expiryDate) // Thời gian hết hạn
                .signWith(secretKey, SignatureAlgorithm.HS256) // Ký token với khóa và thuật toán
                .compact(); // Xây dựng chuỗi token
    }

    private static Claims getAllClaimsFromToken(String token, Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, Key secretKey) {
        final Claims claims = getAllClaimsFromToken(token, secretKey);
        return claimsResolver.apply(claims);
    }

    public static boolean isTokenExpired(String token, Key secretKey) {
        try {
            final Date expiration = getClaimFromToken(token, Claims::getExpiration, secretKey);
            return expiration.before(new Date());
        } catch (Exception e) {
            // Nếu có bất kỳ lỗi nào khi parse (ví dụ: chữ ký sai), coi như token không hợp lệ/hết hạn.
            return true;
        }
    }

  
    public static boolean validateToken(String token, Key secretKey) {
        try {
            getAllClaimsFromToken(token, secretKey);
            return !isTokenExpired(token, secretKey);
        } catch (Exception e) {
            return false;
        }
    }
}