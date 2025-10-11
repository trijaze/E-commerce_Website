package util;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtConfig {

    // THỜI GIAN HẾT HẠN CỦA TOKEN
    public static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000; // 15 phút
    public static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 ngày

    // KHÓA BÍ MẬT TĨNH
    private static final String JWT_SECRET_STRING = "DayLaMotChuoiBiMatRatDaiVaAnToanDungDeMaHoaJWTTokenCuaToi";
    private static final String JWT_REFRESH_SECRET_STRING = "DayLaMotChuoiBiMatKhacCungRatDaiVaAnToanDungChoRefreshToken";

    public static final SecretKey JWT_SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    public static final SecretKey JWT_REFRESH_SECRET_KEY = Keys.hmacShaKeyFor(JWT_REFRESH_SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // Khối này sẽ được thực thi một lần duy nhất khi lớp JwtConfig được load.
    static {
        System.out.println("--- [JwtConfig] Class Loaded ---");
        System.out.println("--- [JwtConfig] JWT_SECRET_STRING used: " + JWT_SECRET_STRING);
        System.out.println("------------------------------");
    }
}

