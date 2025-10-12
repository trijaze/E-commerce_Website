package vn.bachhoa.util;

import vn.bachhoa.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.security.Key;
public class JWTUtil {
	    // Thời gian hết hạn
	   public static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000; // 15 phút
	   public static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 ngày

	    // Chuỗi bí mật tĩnh
	   public static final String JWT_SECRET_STRING = "DayLaMotChuoiBiMatRatDaiVaAnToanDungDeMaHoaJWTTokenCuaToi";
	   public static final String JWT_REFRESH_SECRET_STRING = "DayLaMotChuoiBiMatKhacCungRatDaiVaAnToanDungChoRefreshToken";

	    // Khóa bí mật tĩnh
	   public static final SecretKey JWT_SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET_STRING.getBytes(StandardCharsets.UTF_8));
	   public static final SecretKey JWT_REFRESH_SECRET_KEY = Keys.hmacShaKeyFor(JWT_REFRESH_SECRET_STRING.getBytes(StandardCharsets.UTF_8));

	   public static String generateAccessToken(User user) {
	        return generateToken(user, ACCESS_TOKEN_EXPIRATION_MS, JWT_SECRET_KEY);
	   }
	   
	   public static String generateRefreshToken(User user) {
		   return generateToken(user, REFRESH_TOKEN_EXPIRATION_MS, JWT_REFRESH_SECRET_KEY);
	   }

	   //Phương thức xây dựng JWT.
	   public static String generateToken(User user, long expirationMs, Key secretKey) {
	       long nowMillis = System.currentTimeMillis();
	       return Jwts.builder()
	                .setSubject(user.getUsername())
	                .claim("userId", user.getUserId())
	                .claim("role", user.getRole())
	                .setIssuedAt(new Date(nowMillis))
	                .setExpiration(new Date(nowMillis + expirationMs))
	                .signWith(secretKey, SignatureAlgorithm.HS256)
	                .compact();
	    }
	}
