package vn.bachhoa.promotions;

import vn.bachhoa.common.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {

    // Create promotion + mapping categories (transactional)
    public int create(Promotion p) throws Exception {
        String insertSql = "INSERT INTO promotions (code, title, description, discountType, discountValue, minOrderAmount, active, startAt, endAt, createdBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertMap = "INSERT INTO promotion_categories (promotionId, categoryId) VALUES (?, ?)";
        try (Connection c = Db.get().getConnection();
             PreparedStatement ps = c.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            c.setAutoCommit(false);
            ps.setString(1, p.getCode());
            ps.setString(2, p.getTitle());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getDiscountType());
            ps.setDouble(5, p.getDiscountValue());
            ps.setDouble(6, p.getMinOrderAmount() == null ? 0.0 : p.getMinOrderAmount());
            ps.setBoolean(7, p.getActive() == null ? true : p.getActive());
            ps.setTimestamp(8, Timestamp.valueOf(p.getStartAt()));
            ps.setTimestamp(9, Timestamp.valueOf(p.getEndAt()));
            if (p.getCreatedBy() == null) ps.setNull(10, Types.INTEGER);
            else ps.setInt(10, p.getCreatedBy());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                c.rollback();
                throw new SQLException("Creating promotion failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int promoId = keys.getInt(1);
                    // insert mappings
                    if (p.getCategoryIds() != null && !p.getCategoryIds().isEmpty()) {
                        try (PreparedStatement mapPs = c.prepareStatement(insertMap)) {
                            for (Integer catId : p.getCategoryIds()) {
                                mapPs.setInt(1, promoId);
                                mapPs.setInt(2, catId);
                                mapPs.addBatch();
                            }
                            mapPs.executeBatch();
                        }
                    }
                    c.commit();
                    return promoId;
                } else {
                    c.rollback();
                    throw new SQLException("Creating promotion failed, no ID obtained.");
                }
            }
        }
    }

    // Update promotion and category mapping
    public void update(Promotion p) throws Exception {
        String updSql = "UPDATE promotions SET code=?, title=?, description=?, discountType=?, discountValue=?, minOrderAmount=?, active=?, startAt=?, endAt=?, updatedAt=NOW() WHERE id=?";
        String deleteMap = "DELETE FROM promotion_categories WHERE promotionId=?";
        String insertMap = "INSERT INTO promotion_categories (promotionId, categoryId) VALUES (?, ?)";
        try (Connection c = Db.get().getConnection();
             PreparedStatement ps = c.prepareStatement(updSql)) {
            c.setAutoCommit(false);
            ps.setString(1, p.getCode());
            ps.setString(2, p.getTitle());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getDiscountType());
            ps.setDouble(5, p.getDiscountValue());
            ps.setDouble(6, p.getMinOrderAmount() == null ? 0.0 : p.getMinOrderAmount());
            ps.setBoolean(7, p.getActive() == null ? true : p.getActive());
            ps.setTimestamp(8, Timestamp.valueOf(p.getStartAt()));
            ps.setTimestamp(9, Timestamp.valueOf(p.getEndAt()));
            ps.setInt(10, p.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                c.rollback();
                throw new SQLException("Update promotion failed or not found");
            }
            // replace mappings
            try (PreparedStatement del = c.prepareStatement(deleteMap)) {
                del.setInt(1, p.getId());
                del.executeUpdate();
            }
            if (p.getCategoryIds() != null && !p.getCategoryIds().isEmpty()) {
                try (PreparedStatement mapPs = c.prepareStatement(insertMap)) {
                    for (Integer catId : p.getCategoryIds()) {
                        mapPs.setInt(1, p.getId());
                        mapPs.setInt(2, catId);
                        mapPs.addBatch();
                    }
                    mapPs.executeBatch();
                }
            }
            c.commit();
        }
    }

    // Delete promotion (cascade removes promotion_categories)
    public void delete(int id) throws Exception {
        try (Connection c = Db.get().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM promotions WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Find by ID (with category ids)
    public Promotion findById(int id) throws Exception {
        String sql = "SELECT * FROM promotions WHERE id = ?";
        try (Connection c = Db.get().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Promotion p = mapRow(rs);
                    p.setCategoryIds(loadCategoryIds(c, p.getId()));
                    return p;
                }
            }
        }
        return null;
    }

    // Find by code (active only)
    public Promotion findByCode(String code) throws Exception {
        String sql = "SELECT * FROM promotions WHERE code = ? AND active = 1";
        try (Connection c = Db.get().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Promotion p = mapRow(rs);
                    p.setCategoryIds(loadCategoryIds(c, p.getId()));
                    return p;
                }
            }
        }
        return null;
    }

    // List all promotions
    public List<Promotion> listAll() throws Exception {
        List<Promotion> out = new ArrayList<>();
        String sql = "SELECT * FROM promotions ORDER BY startAt DESC";
        try (Connection c = Db.get().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Promotion p = mapRow(rs);
                p.setCategoryIds(loadCategoryIds(c, p.getId()));
                out.add(p);
            }
        }
        return out;
    }

    // List promotions currently active for a category (based on current timestamp)
    public List<Promotion> listActiveForCategory(int categoryId) throws Exception {
        List<Promotion> out = new ArrayList<>();
        String sql = "SELECT pr.* FROM promotions pr JOIN promotion_categories pc ON pr.id = pc.promotionId WHERE pc.categoryId = ? AND pr.active = 1 AND pr.startAt <= NOW() AND pr.endAt >= NOW() ORDER BY pr.startAt";
        try (Connection c = Db.get().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Promotion p = mapRow(rs);
                    p.setCategoryIds(loadCategoryIds(c, p.getId()));
                    out.add(p);
                }
            }
        }
        return out;
    }

    private List<Integer> loadCategoryIds(Connection c, int promotionId) throws SQLException {
        List<Integer> cats = new ArrayList<>();
        String sql = "SELECT categoryId FROM promotion_categories WHERE promotionId = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cats.add(rs.getInt("categoryId"));
            }
        }
        return cats;
    }

    private Promotion mapRow(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setId(rs.getInt("id"));
        p.setCode(rs.getString("code"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setDiscountType(rs.getString("discountType"));
        p.setDiscountValue(rs.getDouble("discountValue"));
        p.setMinOrderAmount(rs.getDouble("minOrderAmount"));
        p.setActive(rs.getBoolean("active"));
        Timestamp st = rs.getTimestamp("startAt");
        Timestamp ed = rs.getTimestamp("endAt");
        if (st != null) p.setStartAt(st.toLocalDateTime());
        if (ed != null) p.setEndAt(ed.toLocalDateTime());
        p.setCreatedBy(rs.getObject("createdBy") == null ? null : rs.getInt("createdBy"));
        return p;
    }
}
