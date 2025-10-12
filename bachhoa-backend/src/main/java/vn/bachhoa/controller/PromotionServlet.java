package vn.bachhoa.controller;

import vn.bachhoa.dao.PromotionDAO;
import vn.bachhoa.model.Promotion;
import vn.bachhoa.util.JsonUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/promotions")
public class PromotionServlet extends HttpServlet {
    private PromotionDAO promotionDAO = new PromotionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Promotion> list = promotionDAO.findAll();
        JsonUtil.sendJsonResponse(resp, list);
    }
}
