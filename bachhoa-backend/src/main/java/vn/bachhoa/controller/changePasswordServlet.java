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
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@WebServlet("/api/secure/users/changepassword")
public class changePasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserDAO userDAO = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private static class ChangePasswordRequest {
        String oldPassword;
        String newPassword;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Integer userId = (Integer) req.getAttribute("userId");
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
            return;
        }

        BufferedReader reader = req.getReader();
        ChangePasswordRequest payload = gson.fromJson(reader, ChangePasswordRequest.class);

        if (payload == null || payload.oldPassword == null || payload.newPassword == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        User u = userDAO.findById(userId);
        if (u == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy người dùng.");
            return;
        }

        String hashedOldPassword = PasswordUtil.hashPassword(payload.oldPassword);
        if (!u.getPasswordHash().equals(hashedOldPassword)) {
            sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Mật khẩu cũ không chính xác.");
            return;
        }

        // --- NẾU XÁC THỰC THÀNH CÔNG, CẬP NHẬT MẬT KHẨU VÀ GHI NHẬT KÝ ---
        try {
            // Cập nhật mật khẩu mới
            u.setPasswordHash(PasswordUtil.hashPassword(payload.newPassword));
            userDAO.update(u);

            AuditLog log = new AuditLog(userId, "CHANGE_PASSWORD", "Users", String.valueOf(userId));
            auditLogDAO.save(log);

            // Gửi phản hồi thành công
            sendJsonResponse(resp, HttpServletResponse.SC_OK, Map.of("message", "Đổi mật khẩu thành công."));

        } catch (Exception e) {
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi trong quá trình cập nhật.");
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

