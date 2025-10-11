package common;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import jpa.JPAUtil;

/** Helper dùng JPA ở mọi nơi trong project. */
public final class Jpa {
  private Jpa() {}

  /** Lấy EntityManager (NHỚ close sau khi dùng). */
  public static EntityManager em() {
    return JPAUtil.EMF.createEntityManager();
  }

  /** Chạy tác vụ trong transaction và trả về kết quả. */
  public static <T> T tx(Function<EntityManager, T> fn) {
    EntityManager em = em();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      T result = fn.apply(em);
      tx.commit();
      return result;
    } catch (RuntimeException ex) {
      if (tx.isActive()) tx.rollback();
      throw ex;
    } finally {
      em.close();
    }
  }

  /** Chạy tác vụ trong transaction (không trả kết quả). */
  public static void tx(Consumer<EntityManager> fn) {
    tx(em -> { fn.accept(em); return null; });
  }
}
