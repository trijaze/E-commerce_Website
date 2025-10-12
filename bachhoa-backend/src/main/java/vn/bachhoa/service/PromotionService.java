package vn.bachhoa.service;

import vn.bachhoa.dao.PromotionDAO;
import vn.bachhoa.model.Promotion;

import java.util.List;

/**
 * PromotionService: business layer (validation, rules) before DAO calls.
 */
public class PromotionService {
    private final PromotionDAO dao = new PromotionDAO();

    public List<Promotion> listAll() {
        return dao.listAll();
    }

    public Promotion findById(int id) {
        return dao.findById(id);
    }

    public Promotion findByCode(String code) {
        return dao.findByCode(code);
    }

    public int createPromotion(Promotion p, List<Integer> categoryIds) throws Exception {
        // basic validation
        if (p.getCode() == null || p.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion code is required");
        }
        if (p.getDiscountValue() == null) {
            throw new IllegalArgumentException("discountValue is required");
        }
        return dao.create(p, categoryIds);
    }

    public void updatePromotion(Promotion p, List<Integer> categoryIds) throws Exception {
        if (p.getId() == null) throw new IllegalArgumentException("Promotion id is required");
        dao.update(p, categoryIds);
    }

    public void deletePromotion(int id) throws Exception {
        dao.delete(id);
    }

    public List<Promotion> listActiveForCategory(int categoryId) {
        return dao.listActiveForCategory(categoryId);
    }
}
