package vn.bachhoa.dao;

import vn.bachhoa.model.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.function.Function;
import vn.bachhoa.dto.UserBasicDTO;

/**
 * Lớp DAO (Data Access Object) chịu trách nhiệm cho tất cả các thao tác
 * truy cập cơ sở dữ liệu liên quan đến đối tượng User.
*/
public class UserDAO {
    // EntityManagerFactory nên là static và final, được khởi tạo một lần duy nhất cho toàn bộ ứng dụng.
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bachhoaPU");

    /**
     * Phương thức private helper để quản lý vòng đời của EntityManager.
     * Nó tự động xử lý việc tạo, đóng và rollback transaction khi có lỗi,
     * giúp các phương thức public trở nên gọn gàng hơn rất nhiều.
     */
    private <T> T executeInsideTransaction(Function<EntityManager, T> function) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = function.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e; // Ném lại ngoại lệ để lớp service có thể xử lý
        } finally {
            em.close();
        }
    }

    // Tìm người dùng bằng ID.
    public User findById(int id) {
        return executeInsideTransaction(em -> em.find(User.class, id));
    }
    public UserBasicDTO findBasicById(int id) {
        return executeInsideTransaction(em -> {
            TypedQuery<UserBasicDTO> query = em.createQuery(
                "SELECT new vn.bachhoa.dto.UserBasicDTO(u.userId, u.username, u.phoneNumber, u.email, u.createdAt) " +
                "FROM User u WHERE u.userId = :id",
                UserBasicDTO.class
            );
            query.setParameter("id", id);
            try {
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }


    //Tìm người dùng bằng identifier (username hoặc phone).
    public User findByIdentifier(String identifier) {
        return executeInsideTransaction(em -> {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :identifier OR u.phoneNumber = :identifier", User.class);
            query.setParameter("identifier", identifier);
            try {
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null; // Trả về null một cách tường minh khi không có kết quả
            }
        });
    }

    // Kiểm tra xem một username đã tồn tại hay chưa.
    public boolean usernameExists(String username) {
        return findByIdentifier(username) != null;
    }

    // Kiểm tra xem một số điện thoại đã tồn tại hay chưa
    public boolean phoneExists(String phoneNumber) {
        return findByIdentifier(phoneNumber) != null;
    }
    
    // Kiểm tra xem một email đã tồn tại hay chưa
    public boolean emailExists(String email) {
        return executeInsideTransaction(em -> {
            Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                           .setParameter("email", email)
                           .getSingleResult();
            return count > 0;
        });
    }

    // Lưu một đối tượng User mới vào cơ sở dữ liệu (dùng cho đăng ký).
    public void save(User user) {
        executeInsideTransaction(em -> {
            em.persist(user);
            return null; // Không cần trả về gì cả
        });
    }

    //Cập nhật thông tin của một User đã có trong cơ sở dữ liệu.
    public void update(User user) {
        executeInsideTransaction(em -> {
            em.merge(user);
            return null; // Không cần trả về gì cả
        });
    }

    //Xóa một người dùng khỏi cơ sở dữ liệu.
    public void delete(int userId) {
        executeInsideTransaction(em -> {
            User user = em.find(User.class, userId);
            if (user != null) {
                em.remove(user);
            }
            return null;
        });
    }
}
