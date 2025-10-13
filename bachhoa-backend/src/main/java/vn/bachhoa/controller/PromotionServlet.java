package vn.bachhoa.controller;

import vn.bachhoa.model.Promotion;
import vn.bachhoa.service.PromotionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class PromotionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final PromotionService service = new PromotionService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        mapper.registerModule(new JavaTimeModule()); // support LocalDateTime
    }

    // GET /api/promotions -> listAll
    // GET /api/promotions?categoryId=1 OR ?productId=.. OR ?variantId=.. OR ?code=XYZ
    // GET /api/promotions/{id}
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo(); // /{id} or null
        String categoryId = req.getParameter("categoryId");
        String productId = req.getParameter("productId");
        String variantId = req.getParameter("variantId");
        String code = req.getParameter("code");

        if (pathInfo != null && pathInfo.length() > 1) {
            // /{id}
            String idStr = pathInfo.substring(1);
            try {
                Integer id = Integer.valueOf(idStr);
                Promotion p = service.getById(id);
                if (p == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", "Not found"));
                    return;
                }
                mapper.writeValue(resp.getWriter(), p);
                return;
            } catch (NumberFormatException ex) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", "Invalid id"));
                return;
            }
        }

        try {
            if (code != null && !code.isEmpty()) {
                Promotion p = service.getByCode(code);
                if (p == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", "Not found"));
                } else {
                    mapper.writeValue(resp.getWriter(), p);
                }
                return;
            }

            if (categoryId != null) {
                List<Promotion> list = service.listActiveForCategory(Integer.valueOf(categoryId));
                mapper.writeValue(resp.getWriter(), list);
                return;
            }
            if (productId != null) {
                List<Promotion> list = service.listActiveForProduct(Integer.valueOf(productId));
                mapper.writeValue(resp.getWriter(), list);
                return;
            }
            if (variantId != null) {
                List<Promotion> list = service.listActiveForVariant(Integer.valueOf(variantId));
                mapper.writeValue(resp.getWriter(), list);
                return;
            }

            // default: list all
            List<Promotion> list = service.listAll();
            mapper.writeValue(resp.getWriter(), list);
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", ex.getMessage()));
        }
    }

    // POST /api/promotions -> create
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Promotion p = mapper.readValue(req.getInputStream(), Promotion.class);
            Promotion created = service.createPromotion(p);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), created);
        } catch (IllegalArgumentException iae) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", iae.getMessage()));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", ex.getMessage()));
        }
    }

    // PUT /api/promotions/{id}
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", "id required"));
            return;
        }
        try {
            Integer id = Integer.valueOf(pathInfo.substring(1));
            Promotion p = mapper.readValue(req.getInputStream(), Promotion.class);
            p.setId(id); // ensure id
            Promotion updated = service.updatePromotion(p);
            mapper.writeValue(resp.getWriter(), updated);
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", "invalid id"));
        } catch (IllegalArgumentException iae) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", iae.getMessage()));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", ex.getMessage()));
        }
    }

    // DELETE /api/promotions/{id}
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", "id required"));
            return;
        }
        try {
            Integer id = Integer.valueOf(pathInfo.substring(1));
            service.deletePromotion(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", "invalid id"));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), java.util.Collections.singletonMap("error", ex.getMessage()));
        }
    }
}
