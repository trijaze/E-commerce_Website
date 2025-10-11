package com.hoa.servlets;

import com.hoa.dao.CartDAO;
import com.hoa.model.CartItem;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final CartDAO dao = new CartDAO();
    private final Gson gson = new Gson();

    // 🔧 Hàm tiện ích: thêm header CORS cho mọi request
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*"); // Cho phép FE ở port khác (React)
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setContentType("application/json; charset=UTF-8");
    }

    // 🔧 Xử lý preflight request (CORS)
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // 🟢 Lấy danh sách sản phẩm trong giỏ
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        List<CartItem> items = dao.getAll();
        resp.getWriter().write(gson.toJson(items));
    }

    // 🟢 Thêm sản phẩm mới vào giỏ
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);

        BufferedReader reader = req.getReader();
        CartItem item = gson.fromJson(reader, CartItem.class);

        try {
            dao.addItem(item);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"✅ Added successfully\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"❌ Failed to add item\"}");
            e.printStackTrace();
        }
    }

    // 🟢 Cập nhật số lượng sản phẩm
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);

        BufferedReader reader = req.getReader();
        CartItem item = gson.fromJson(reader, CartItem.class);

        try {
            dao.updateQuantity(item.getId(), item.getQuantity());
            resp.getWriter().write("{\"message\": \"🔄 Updated successfully\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"❌ Failed to update\"}");
            e.printStackTrace();
        }
    }

    // 🟢 Xóa sản phẩm khỏi giỏ
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            dao.deleteItem(id);
            resp.getWriter().write("{\"message\": \"🗑️ Deleted successfully\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"❌ Invalid request or item not found\"}");
        }
    }
}
