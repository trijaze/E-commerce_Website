package jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class JPAUtil implements ServletContextListener {
  public static EntityManagerFactory EMF;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    EMF = Persistence.createEntityManagerFactory("ecommercePU");
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    if (EMF != null && EMF.isOpen()) EMF.close();
  }
}
