package vn.bachhoa.controller;

import vn.bachhoa.dao.UserDAO;
import vn.bachhoa.dao.AuditLogDAO; 
import vn.bachhoa.model.User;
import vn.bachhoa.model.AuditLog;
import vn.bachhoa.util.LocalDateTimeAdapter;
import vn.bachhoa.util.PasswordUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Servlet xử lý chức năng "Quên mật khẩu".
 * Người dùng cần cung cấp username và SĐT để xác thực trước khi đặt lại mật khẩu mới.
 */
@WebServlet("/api/auth/forgotPassword")
public class resetPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO(); 
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    // DTO để nhận dữ liệu từ JSON request
    private static class ResetRequest {
        String username;
        String phoneNumber;
        String newPassword;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        
        BufferedReader reader = req.getReader();
        ResetRequest cred = gson.fromJson(reader, ResetRequest.class);

        // --- KIỂM TRA DỮ LIỆU ĐẦU VÀO ---
        if (cred == null || cred.username == null || cred.username.trim().isEmpty() ||
            cred.phoneNumber == null || cred.phoneNumber.trim().isEmpty() ||
            cred.newPassword == null || cred.newPassword.trim().isEmpty()) {
            
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        // --- XÁC THỰC NGƯỜI DÙNG ---
        User u = userDAO.findByIdentifier(cred.username);
        
        if (u == null || !u.getPhoneNumber().equals(cred.phoneNumber)) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Tên đăng nhập hoặc số điện thoại không chính xác.");
            return;
        }

        // --- CẬP NHẬT MẬT KHẨU MỚI VÀ GHI NHẬT KÝ ---
        try {
            // Cập nhật mật khẩu mới đã được hash
            u.setPasswordHash(PasswordUtil.hashPassword(cred.newPassword));
            userDAO.update(u);
            
            // GHI NHẬT KÝ HÀNH ĐỘNG ĐẶT LẠI MẬT KHẨU
            AuditLog log = new AuditLog(u.getUserId(), "RESET_PASSWORD", "Users", String.valueOf(u.getUserId()));
            auditLogDAO.save(log);
            
            // Gửi phản hồi thành công
            sendJsonResponse(resp, HttpServletResponse.SC_OK, Map.of("message", "Đặt lại mật khẩu thành công."));

        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi trong quá trình đặt lại mật khẩu.");
        }
    }
    
    //Phương thức gửi response lỗi dưới dạng JSON.
    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        sendJsonResponse(resp, statusCode, Map.of("error", message));
    }

    //Phương thức để gửi response JSON.
    private void sendJsonResponse(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(data));
    }
}

