package vn.bachhoa.common;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class Db {
  private static HikariDataSource ds;
  static {
    try {
      Properties p = new Properties();
      p.load(Db.class.getClassLoader().getResourceAsStream("application.properties"));
      HikariConfig cfg = new HikariConfig();
      cfg.setJdbcUrl(p.getProperty("db.url"));
      cfg.setUsername(p.getProperty("db.username"));
      cfg.setPassword(p.getProperty("db.password"));
      cfg.setDriverClassName("com.mysql.cj.jdbc.Driver"); 
      cfg.setMaximumPoolSize(10);
      ds = new HikariDataSource(cfg);
    } catch (IOException e) {
      throw new RuntimeException("Cannot load DB config", e);
    }
  }
  public static DataSource get() { return ds; }
}
