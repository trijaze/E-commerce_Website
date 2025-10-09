package vn.bachhoa.auth;

import vn.bachhoa.common.Db;
import vn.bachhoa.common.Json;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        try (Scanner sc = new Scanner(req.getInputStream()).useDelimiter("\\A")) {
            String body = sc.hasNext() ? sc.next() : "{}";
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> data = mapper.readValue(body, Map.class);

            if ("/login".equals(path)) {
                try (Connection conn = Db.get().getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                            "SELECT id, name, email, role FROM users WHERE email=? AND password=?");
                    ps.setString(1, (String) data.get("email"));
                    ps.setString(2, (String) data.get("password"));
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("id", rs.getInt("id"));
                        user.put("name", rs.getString("name"));
                        user.put("email", rs.getString("email"));
                        user.put("role", rs.getString("role"));
                        req.getSession().setAttribute("user", user);
                        Json.write(resp, 200, Map.of("ok", true, "data", user));
                    } else {
                        Json.write(resp, 401, Map.of("ok", false, "error", "Invalid credentials"));
                    }
                }
            } else if ("/register".equals(path)) {
                try (Connection conn = Db.get().getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
                    ps.setString(1, (String) data.get("name"));
                    ps.setString(2, (String) data.get("email"));
                    ps.setString(3, (String) data.get("password"));
                    ps.executeUpdate();
                    Json.write(resp, 201, Map.of("ok", true));
                }
            }
        } catch (Exception e) {
            Json.write(resp, 500, Map.of("ok", false, "error", e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if ("/me".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                Json.write(resp, 200, Map.of("ok", true, "data", session.getAttribute("user")));
            } else {
                Json.write(resp, 401, Map.of("ok", false, "error", "Not logged in"));
            }
        }
    }
}
