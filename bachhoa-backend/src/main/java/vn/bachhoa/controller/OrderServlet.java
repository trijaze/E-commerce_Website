package vn.bachhoa.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vn.bachhoa.dao.OrderDao;
import vn.bachhoa.model.Order;
import vn.bachhoa.model.OrderItem;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private final OrderDao orderDao = new OrderDao();

    // ✅ Dùng GsonBuilder để loại bỏ field không có @Expose (tránh vòng lặp vô hạn)
    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setContentType("application/json; charset=UTF-8");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCors(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // 🟢 Lấy danh sách hoặc 1 đơn hàng cụ thể
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        try {
            String id = req.getParameter("id");

            if (id != null) {
                Order order = orderDao.getById(Integer.parseInt(id));
                if (order == null) {
                    resp.getWriter().write("{\"error\": \"Order not found\"}");
                    return;
                }
                resp.getWriter().write(gson.toJson(order));
            } else {
                List<Order> orders = orderDao.getAll();
                resp.getWriter().write(gson.toJson(orders));
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"❌ Failed to fetch orders\"}");
        }
    }

    // 🟢 Tạo đơn hàng mới
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);

        try (BufferedReader reader = req.getReader()) {
            Map<String, Object> data = gson.fromJson(reader, Map.class);

            int userId = ((Double) data.get("userId")).intValue();
            String paymentMethod = (String) data.get("paymentMethod");
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");

            double total = 0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (Map<String, Object> item : items) {
                int productId = ((Double) item.get("productId")).intValue();
                int quantity = ((Double) item.get("quantity")).intValue();
                double price = (Double) item.get("price");
                total += price * quantity;

                OrderItem oi = new OrderItem();
                oi.setProductId(productId);
                oi.setQuantity(quantity);
                oi.setPrice(price);
                oi.setStatus("pending_payment");
                orderItems.add(oi);
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setPaymentMethod(paymentMethod);
            order.setTotal(total);
            order.setStatus("pending_payment");
            order.setItems(orderItems);

            for (OrderItem oi : orderItems) oi.setOrder(order);

            orderDao.createOrder(order);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"✅ Order created successfully\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"❌ Failed to create order\"}");
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String id = req.getParameter("id");
            if (id == null) {
                resp.getWriter().write("{\"error\": \"Missing order id\"}");
                return;
            }

            BufferedReader reader = req.getReader();
            Map<String, Object> data = new Gson().fromJson(reader, Map.class);

            Order order = orderDao.getById(Integer.parseInt(id));
            if (order == null) {
                resp.getWriter().write("{\"error\": \"Order not found\"}");
                return;
            }

            if (data.containsKey("status")) order.setStatus((String) data.get("status"));
            if (data.containsKey("paymentMethod")) order.setPaymentMethod((String) data.get("paymentMethod"));

            orderDao.update(order);
            resp.getWriter().write("{\"message\": \"✅ Order updated successfully\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"error\": \"❌ Failed to update order\"}");
        }
    }

}
