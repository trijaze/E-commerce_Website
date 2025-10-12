package vn.bachhoa.dao;

import vn.bachhoa.model.Order;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.*;
import java.util.List;

public class OrderDao {

    private final EntityManager em = JPAUtil.getEntityManager();

    // 🟢 Tạo đơn hàng mới
    public void createOrder(Order order) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(order);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    // 🟢 Lấy tất cả đơn hàng
    public List<Order> getAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    // 🟢 Lấy đơn hàng theo ID
    public Order getById(int id) {
        return em.find(Order.class, id);
    }

    // 🟢 Cập nhật đơn hàng (status, paymentMethod, hoặc bất kỳ field nào khác)
    public void update(Order order) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(order); // ✅ merge để JPA tự cập nhật entity trong DB
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    // 🟢 Xoá đơn hàng
    public void deleteOrder(int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Order order = em.find(Order.class, id);
            if (order != null) em.remove(order);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
}
