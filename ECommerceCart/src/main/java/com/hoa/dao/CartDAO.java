package com.hoa.dao;

import com.hoa.model.CartItem;
import javax.persistence.*;
import java.util.List;

public class CartDAO {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ECommerceCartPU");

    public List<CartItem> getAll() {
        EntityManager em = emf.createEntityManager();
        List<CartItem> list = em.createQuery("SELECT c FROM CartItem c", CartItem.class).getResultList();
        em.close();
        return list;
    }

    public void addItem(CartItem item) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(item);
        em.getTransaction().commit();
        em.close();
    }

    public void updateQuantity(int id, int quantity) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        CartItem item = em.find(CartItem.class, id);
        if (item != null) item.setQuantity(quantity);
        em.getTransaction().commit();
        em.close();
    }

    public void deleteItem(int id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        CartItem item = em.find(CartItem.class, id);
        if (item != null) em.remove(item);
        em.getTransaction().commit();
        em.close();
    }
}
