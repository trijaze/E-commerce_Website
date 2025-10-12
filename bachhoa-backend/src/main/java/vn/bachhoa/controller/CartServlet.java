package vn.bachhoa.controller;

import vn.bachhoa.dao.CartDAO;
import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.model.Cart;
import vn.bachhoa.model.CartItem;
import vn.bachhoa.model.Product;
import vn.bachhoa.model.User;
import vn.bachhoa.util.JsonUtil;

import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/cart", "/api/cart/add", "/api/cart/remove", "/api/cart/clear"})
public class CartServlet extends HttpServlet {
    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        // Giả sử user đang login -> session có attribute userId
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) { resp.setStatus(401); resp.getWriter().write("{\"message\":\"Unauthorized\"}"); return; }

        List<CartItem> items = cartDAO.getCartItemsByUser(userId);
        resp.getWriter().write(gson.toJson(items));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getServletPath();
        Map<?,?> body = gson.fromJson(req.getReader(), Map.class);

        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) { resp.setStatus(401); resp.getWriter().write("{\"message\":\"Unauthorized\"}"); return; }

        if (path.endsWith("/add")) {
            Double pidDouble = (Double) body.get("productId");
            int productId = pidDouble.intValue();
            Double quantityDouble = (Double) body.get("quantity");
            int quantity = quantityDouble.intValue();

            Product p = productDAO.findById(productId);
            if (p == null) { resp.setStatus(404); resp.getWriter().write("{\"message\":\"Product not found\"}"); return; }

            cartDAO.addToCart(userId, productId, quantity);
            resp.getWriter().write("{\"message\":\"Added to cart\"}");
        } else if (path.endsWith("/remove")) {
            Double pidDouble = (Double) body.get("productId");
            int productId = pidDouble.intValue();
            cartDAO.removeFromCart(userId, productId);
            resp.getWriter().write("{\"message\":\"Removed\"}");
        } else if (path.endsWith("/clear")) {
            cartDAO.clearCart(userId);
            resp.getWriter().write("{\"message\":\"Cart cleared\"}");
        }
    }
}
