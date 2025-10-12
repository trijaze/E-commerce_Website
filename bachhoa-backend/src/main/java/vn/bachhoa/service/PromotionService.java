package vn.bachhoa.service;

import vn.bachhoa.dao.PromotionDAO;
import vn.bachhoa.model.Promotion;

import java.util.List;

public class PromotionService {

    private final PromotionDAO dao = new PromotionDAO();

    public Promotion createPromotion(Promotion p) {
        // could add validation (code uniqueness, dates sanity) here
        if (p.getCode() == null || p.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("code is required");
        }
        // additional checks (e.g., startAt <= endAt) can be added
        return dao.create(p);
    }

    public Promotion updatePromotion(Promotion p) {
        if (p.getId() == null) throw new IllegalArgumentException("id is required");
        return dao.update(p);
    }

    public void deletePromotion(Integer id) {
        dao.delete(id);
    }

    public Promotion getById(Integer id) {
        return dao.findById(id);
    }

    public Promotion getByCode(String code) {
        return dao.findByCode(code);
    }

    public List<Promotion> listAll() {
        return dao.listAll();
    }

    public List<Promotion> listActiveForCategory(Integer categoryId) {
        return dao.listActiveForCategory(categoryId);
    }

    public List<Promotion> listActiveForProduct(Integer productId) {
        return dao.listActiveForProduct(productId);
    }

    public List<Promotion> listActiveForVariant(Integer variantId) {
        return dao.listActiveForVariant(variantId);
    }
}
