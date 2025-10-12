package vn.bachhoa.controller;

import vn.bachhoa.model.Promotion;
import vn.bachhoa.service.PromotionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints:
 * GET  /api/promotions                 -> list all
 * GET  /api/promotions?id=...          -> get by id
 * GET  /api/promotions?categoryId=...  -> list active for category
 * POST /api/promotions                 -> create (body JSON)
 * PUT  /api/promotions/:id             -> update (body JSON)  (we implement using query param id)
 * DELETE /api/promotions?id=...        -> delete
 */
@WebServlet(name = "PromotionServlet", urlPatterns = {"/api/promotions"})
public class PromotionServlet extends HttpServlet {

    private final PromotionService service = new PromotionService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String id = req.getParameter("id");
        String categoryId = req.getParameter("categoryId");
        String code = req.getParameter("code");

        if (id != null) {
            Promotion p = service.findById(Integer.parseInt(id));
            if (p == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getWriter(), Map.of("error", "Promotion not found"));
                return;
            }
            mapper.writeValue(resp.getWriter(), p);
            return;
        }

        if (categoryId != null) {
            List<Promotion> list = service.listActiveForCategory(Integer.parseInt(categoryId));
            mapper.writeValue(resp.getWriter(), list);
            return;
        }

        if (code != null) {
            Promotion p = service.findByCode(code);
            if (p == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getWriter(), Map.of("error", "Promotion not found or not active"));
                return;
            }
            mapper.writeValue(resp.getWriter(), p);
            return;
        }

        List<Promotion> all = service.listAll();
        mapper.writeValue(resp.getWriter(), all);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // create promotion. Expect JSON: {promotion: {...}, categoryIds: [1,2]}
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Map body = mapper.readValue(req.getInputStream(), Map.class);
            // map to Promotion + categoryIds
            Promotion p = mapper.convertValue(body.get("promotion"), Promotion.class);
            List<Integer> cats = (List<Integer>) body.get("categoryIds");
            int id = service.createPromotion(p, cats);
            mapper.writeValue(resp.getWriter(), Map.of("success", true, "id", id));
        } catch (IllegalArgumentException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of("error", ex.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // update: expect {promotion: {...}, categoryIds: [..]}
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Map body = mapper.readValue(req.getInputStream(), Map.class);
            Promotion p = mapper.convertValue(body.get("promotion"), Promotion.class);
            List<Integer> cats = (List<Integer>) body.get("categoryIds");
            service.updatePromotion(p, cats);
            mapper.writeValue(resp.getWriter(), Map.of("success", true));
        } catch (IllegalArgumentException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of("error", ex.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        resp.setContentType("application/json;charset=UTF-8");
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), Map.of("error", "id is required"));
            return;
        }
        try {
            service.deletePromotion(Integer.parseInt(id));
            mapper.writeValue(resp.getWriter(), Map.of("success", true));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of("error", ex.getMessage()));
        }
    }
}
