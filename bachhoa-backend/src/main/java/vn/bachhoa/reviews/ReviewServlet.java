package vn.bachhoa.reviews;

import vn.bachhoa.common.Db;
import vn.bachhoa.common.Json;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/api/reviews")
public class ReviewServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String productId = req.getParameter("productId");
    List<Map<String, Object>> list = new ArrayList<>();

    try (Connection c = Db.get().getConnection()) {
      PreparedStatement ps = c.prepareStatement(
        "SELECT r.*, u.name AS userName FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.product_id = ?");
      ps.setInt(1, Integer.parseInt(productId));
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Map<String, Object> o = new LinkedHashMap<>();
        o.put("id", rs.getInt("id"));
        o.put("userName", rs.getString("userName"));
        o.put("rating", rs.getInt("rating"));
        o.put("comment", rs.getString("comment"));
        o.put("createdAt", rs.getTimestamp("created_at"));
        list.add(o);
      }
      Json.write(resp, 200, Map.of("ok", true, "data", list));
    } catch (Exception e) {
      e.printStackTrace();
      Json.write(resp, 500, Map.of("ok", false, "message", e.getMessage()));
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setCharacterEncoding("UTF-8");
    int productId = Integer.parseInt(req.getParameter("productId"));
    int userId = Integer.parseInt(req.getParameter("userId"));
    int rating = Integer.parseInt(req.getParameter("rating"));
    String comment = req.getParameter("comment");

    try (Connection c = Db.get().getConnection()) {
      PreparedStatement ps = c.prepareStatement(
        "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?, ?, ?, ?)");
      ps.setInt(1, productId);
      ps.setInt(2, userId);
      ps.setInt(3, rating);
      ps.setString(4, comment);
      ps.executeUpdate();
      Json.write(resp, 200, Map.of("ok", true, "message", "Đã thêm review"));
    } catch (Exception e) {
      e.printStackTrace();
      Json.write(resp, 500, Map.of("ok", false, "message", e.getMessage()));
    }
  }
}
