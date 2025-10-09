package vn.bachhoa.users;

import vn.bachhoa.common.Db;
import vn.bachhoa.common.Json;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> users = new ArrayList<>();
        try (Connection conn = Db.get().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name, email, role, created_at FROM users")) {
            while (rs.next()) {
                Map<String, Object> u = new HashMap<>();
                u.put("id", rs.getInt("id"));
                u.put("name", rs.getString("name"));
                u.put("email", rs.getString("email"));
                u.put("role", rs.getString("role"));
                u.put("createdAt", rs.getTimestamp("created_at"));
                users.add(u);
            }
            Json.write(resp, 200, Map.of("ok", true, "data", users));
        } catch (Exception e) {
            e.printStackTrace();
            Json.write(resp, 500, Map.of("ok", false, "error", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (Scanner sc = new Scanner(req.getInputStream()).useDelimiter("\\A")) {
            String body = sc.hasNext() ? sc.next() : "{}";
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> data = mapper.readValue(body, Map.class);

            try (Connection conn = Db.get().getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, 'USER')");
                ps.setString(1, (String) data.get("name"));
                ps.setString(2, (String) data.get("email"));
                ps.setString(3, (String) data.get("password"));
                ps.executeUpdate();
                Json.write(resp, 201, Map.of("ok", true, "data", data));
            }
        } catch (Exception e) {
            Json.write(resp, 500, Map.of("ok", false, "error", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // /{id}
        if (path != null && path.length() > 1) {
            int id = Integer.parseInt(path.substring(1));
            try (Connection conn = Db.get().getConnection()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                Json.write(resp, 200, Map.of("ok", true));
            } catch (Exception e) {
                Json.write(resp, 500, Map.of("ok", false, "error", e.getMessage()));
            }
        }
    }
}
