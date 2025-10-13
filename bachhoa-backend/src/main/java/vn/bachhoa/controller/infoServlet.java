package vn.bachhoa.controller;

import vn.bachhoa.dao.UserDAO;
import vn.bachhoa.dto.UserBasicDTO;
import vn.bachhoa.model.User;
import vn.bachhoa.util.LocalDateTimeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Servlet này xử lý việc lấy thông tin của người dùng hiện tại (đã xác thực).
 * Endpoint này được bảo vệ bởi JwtFilter.
 */
@WebServlet("/api/secure/user/me")
public class infoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final UserDAO dao = new UserDAO();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // Lấy userId đã được JwtFilter xác thực và đính kèm vào request.
        // Dùng userId để truy vấn sẽ hiệu quả và chính xác hơn là dùng username.
        Integer userId = (Integer) req.getAttribute("userId");
        
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Token không hợp lệ hoặc bị thiếu thông tin người dùng.\"}");
            return;
        }

        UserBasicDTO u = dao.findBasicById(userId);

        if (u == null) {
            // TH: user đã bị xóa sau khi token được tạo.
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Không tìm thấy người dùng tương ứng với token.\"}");
            return;
        }
        u.setPasswordHash(null);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(u));
    }
}
