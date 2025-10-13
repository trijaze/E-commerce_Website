package vn.bachhoa.controller;

import com.google.gson.Gson;
import vn.bachhoa.dao.AuditLogDAO;  
import vn.bachhoa.model.AuditLog;    
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

//Servlet xử lý việc đăng xuất của người dùng.
@WebServlet("/api/users/logout") 
public class logoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
            AuditLog log = new AuditLog(userId, "USER_LOGOUT", "Users", String.valueOf(userId));
            auditLogDAO.save(log);

            // Gửi phản hồi thành công. Việc xóa token sẽ do client xử lý.
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Map.of("message", "Đăng xuất thành công")));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Đã xảy ra lỗi trong quá trình đăng xuất.\"}");
        }
    }
}
