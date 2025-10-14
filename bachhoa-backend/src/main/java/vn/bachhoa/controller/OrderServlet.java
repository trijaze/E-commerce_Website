package vn.bachhoa.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vn.bachhoa.dao.OrderDao;
import vn.bachhoa.dto.OrderDTO;
import vn.bachhoa.dto.request.OrderRequest;
import vn.bachhoa.model.Order;

import vn.bachhoa.converter.OrderConverter;
import vn.bachhoa.model.OrderItem;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final OrderDao orderDao = new OrderDao();

    private final Gson gson = new GsonBuilder()
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String id = req.getParameter("id");
            if (id != null) {
                Order order = orderDao.getById(Integer.parseInt(id));
                if (order == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Order not found\"}");
                    return;
                }
                // Convert Order sang OrderDTO
                OrderDTO dto = OrderConverter.toDTO(order);
                resp.getWriter().write(gson.toJson(dto));
            } else {
                List<Order> orders = orderDao.getAll();
                // Convert List<Order> sang List<OrderDTO>
                List<OrderDTO> dtos = orders.stream()
                                            .map(OrderConverter::toDTO)
                                            .collect(Collectors.toList());
                resp.getWriter().write(gson.toJson(dtos));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to fetch orders\"}");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        try (BufferedReader reader = req.getReader()) {
            // parse JSON từ FE
            OrderRequest orderReq = gson.fromJson(reader, OrderRequest.class);

            if (orderReq == null || orderReq.items == null || orderReq.items.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Items cannot be empty\"}");
                return;
            }

            // tạo danh sách OrderItem
            List<OrderItem> orderItems = new ArrayList<>();
            double total = 0;
            for (OrderRequest.Item item : orderReq.items) {
                OrderItem oi = new OrderItem();
                oi.setProductId(item.productId);
                oi.setQuantity(item.quantity);
                oi.setPrice(item.price);
                oi.setStatus("pending_payment");
                total += item.price * item.quantity;
                orderItems.add(oi);
            }

            // tạo order
            Order order = new Order();
            order.setPaymentMethod(orderReq.paymentMethod);
            order.setStatus("pending_payment");
            order.setItems(orderItems);
            order.setTotal(orderReq.totalPrice > 0 ? orderReq.totalPrice : total);

            // promoCode optional
            if (orderReq.promotionCode != null && !orderReq.promotionCode.isEmpty()) {
                order.setPromotionCode(orderReq.promotionCode);
            }

            // liên kết OrderItem với Order
            for (OrderItem oi : orderItems) {
                oi.setOrder(order);
            }

            // lưu vào DB
            orderDao.createOrder(order);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Order created successfully\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to create order\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        try {
            String id = req.getParameter("id");
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing order id\"}");
                return;
            }

            BufferedReader reader = req.getReader();
            Map<String, Object> data = gson.fromJson(reader, Map.class);

            Order order = orderDao.getById(Integer.parseInt(id));
            if (order == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Order not found\"}");
                return;
            }

            if (data.containsKey("status")) order.setStatus((String) data.get("status"));
            if (data.containsKey("paymentMethod")) order.setPaymentMethod((String) data.get("paymentMethod"));

            orderDao.update(order);
            resp.getWriter().write("{\"message\":\"Order updated successfully\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to update order\"}");
        }
    }
}
