package vn.bachhoa.dao;

import vn.bachhoa.model.Cart;
import vn.bachhoa.model.CartItem;
import vn.bachhoa.model.Product;
import vn.bachhoa.model.User;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class CartDAO {

    // Lấy toàn bộ sản phẩm trong giỏ hàng của 1 user
    public List<CartItem> getCartItemsByUser(int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<CartItem> q = em.createQuery(
                "SELECT c FROM CartItem c WHERE c.cart.user.id = :uid", CartItem.class);
            q.setParameter("uid", userId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    //  Thêm sản phẩm vào giỏ hàng
    public void addToCart(int userId, int productId, int quantity) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            //  Lấy user và product
            User user = em.find(User.class, userId);
            Product product = em.find(Product.class, productId);

            if (user == null || product == null) {
                System.out.println("⚠️ User hoặc Product không tồn tại!");
                em.getTransaction().rollback();
                return;
            }

            //  Tìm cart của user
            Cart cart = em.createQuery(
                "SELECT c FROM Cart c WHERE c.user.id = :uid", Cart.class)
                .setParameter("uid", userId)
                .getResultStream()
                .findFirst()
                .orElse(null);

            // Nếu user chưa có cart thì tạo mới
            if (cart == null) {
                cart = new Cart();
                cart.setUser(user);
                em.persist(cart);
            }

            //  Kiểm tra xem sản phẩm đã có trong giỏ chưa
            CartItem existing = em.createQuery(
                "SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cid AND ci.product.id = :pid",
                CartItem.class)
                .setParameter("cid", cart.getCartId())
                .setParameter("pid", productId)
                .getResultStream()
                .findFirst()
                .orElse(null);

            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + quantity);
                em.merge(existing);
            } else {
                CartItem ci = new CartItem();
                ci.setCart(cart);       //  liên kết cart
                ci.setProduct(product); //  liên kết product
                ci.setQuantity(quantity);
                em.persist(ci);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    //  Cập nhật số lượng sản phẩm trong giỏ
    public void updateQuantity(int id, int quantity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            CartItem item = em.find(CartItem.class, id);
            if (item != null) {
                item.setQuantity(quantity);
                em.merge(item);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    //  Xóa 1 sản phẩm khỏi giỏ hàng (theo user + product)
    public void removeFromCart(int userId, int productId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            em.createQuery(
                "DELETE FROM CartItem ci WHERE ci.cart.user.id = :uid AND ci.product.id = :pid")
                .setParameter("uid", userId)
                .setParameter("pid", productId)
                .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    //  Xóa 1 sản phẩm theo ID (FE gọi DELETE /api/cart?id=...)
    public void deleteItemById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            CartItem item = em.find(CartItem.class, id);
            if (item != null) {
                em.remove(item);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    //  Xóa toàn bộ giỏ hàng của 1 user
    public void clearCart(int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            em.createQuery(
                "DELETE FROM CartItem ci WHERE ci.cart.user.id = :uid")
                .setParameter("uid", userId)
                .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}
