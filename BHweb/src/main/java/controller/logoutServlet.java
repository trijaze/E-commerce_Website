package controller;

import com.google.gson.Gson;
import dao.AuditLogDAO;  
import entity.AuditLogs;    
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet xử lý việc đăng xuất của người dùng.
 * Endpoint này được bảo vệ bởi JwtFilter để xác định người dùng nào đang đăng xuất.
 * Chức năng chính là ghi lại nhật ký hành động đăng xuất.
 */
@WebServlet("/api/users/logout") 
public class logoutServlet extends HttpServlet {
    private final AuditLogDAO auditLogDAO = new AuditLogDAO(); 
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // Lấy thông tin người dùng từ token đã được xác thực
        // JwtFilter đã xác thực token và đính kèm userId vào request.
        Integer userId = (Integer) req.getAttribute("userId");

        if (userId == null) {
            // Trường hợp này hiếm khi xảy ra nếu JwtFilter hoạt động đúng, nhưng vẫn cần kiểm tra để đảm bảo an toàn.
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"User not authenticated\"}");
            return;
        }

        try {
            // GHI NHẬT KÝ HÀNH ĐỘNG ĐĂNG XUẤT
            AuditLogs log = new AuditLogs(userId, "USER_LOGOUT", "Users", String.valueOf(userId));
            auditLogDAO.save(log);

            // Gửi phản hồi thành công. Việc xóa token sẽ do client xử lý.
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Map.of("message", "Logged out successfully.")));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"An error occurred during logout.\"}");
        }
    }
}
