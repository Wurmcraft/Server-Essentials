/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.db;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import java.util.HashMap;
import java.util.Set;
import javax.persistence.Entity;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections8.Reflections;

/**
 * Helps with the configuration and creation of the database along with ongoing maintenance such as
 * table updates.
 */
public class DatabaseConfigurator {

  public static NonBlockingHashMap<String, String> driverClasses = new NonBlockingHashMap<>();
  public static NonBlockingHashMap<String, String> dialectClasses = new NonBlockingHashMap<>();

  static {
    // Driver
    driverClasses.put("mysql", "com.mysql.jdbc.Driver");
    driverClasses.put("postgresql", "org.postgresql.Driver");
    //    driverClasses.put("postgres", "org.postgresql.Driver");
    // Dialect
    dialectClasses.put("mysql", "org.hibernate.dialect.MySQL8Dialect");
    dialectClasses.put("postgresql", "org.hibernate.dialect.PostgreSQL95Dialect");
  }

  /**
   * Configure the initial DB connection and settings.
   *
   * @param autofillValues values to be used by automatic setup for autofill into setup prompts
   */
  public static boolean initialDBSetup(HashMap<String, String> autofillValues) {
    Configuration config = createConfiguration(autofillValues);
    try {
      SessionFactory sessionFactory = config.buildSessionFactory();
      DatabaseConnector.sessionFactory = sessionFactory;
      Session session = sessionFactory.openSession();
      DatabaseConnector.databaseType = autofillValues.get("sql_name").toLowerCase();
      return session.isConnected();
    } catch (Exception e) {
      ServerEssentialsBackend.LOG.warn("Failed to connect to database.");
      ServerEssentialsBackend.LOG.debug(e.getMessage());
      return false;
    }
  }

  // sql_name = jdbc connector name
  // sql_host = sql server ip
  // sql_port = sql server port
  // sql_username = sql username
  // sql_password = sql password
  private static Configuration createConfiguration(HashMap<String, String> autofillValues) {
    Configuration config = new Configuration();
    config.setProperty(
        "hibernate.connection.driver_class",
        driverClasses.getOrDefault(autofillValues.get("sql_name").toLowerCase(), null));
    config.setProperty(
        "hibernate.connection.url",
        createURL(
            autofillValues.get("sql_name"),
            autofillValues.get("sql_host"),
            autofillValues.get("sql_port"),
            autofillValues.get("sql_table")));
    config.setProperty("hibernate.connection.username", autofillValues.get("sql_username"));
    config.setProperty("hibernate.connection.password", autofillValues.get("sql_password"));
    config.setProperty(
        "hibernate.dialect",
        dialectClasses.getOrDefault(autofillValues.get("sql_name").toLowerCase(), null));
    config.setProperty("hibernate.current_session_context_class", "thread");
    // Table Models
    Reflections reflections = new Reflections("io.wurmatron.server_essentials.backend.model.db");
    Set<Class<?>> importantClasses = reflections.getTypesAnnotatedWith(Entity.class);
    for (Class<?> clazz : importantClasses) {
      config.addAnnotatedClass(clazz);
    }
    config.addPackage("io.wurmatron.server_essentials.backend.model.db");
    return config;
  }

  private static String createURL(String sqlName, String host, String port, String dbName) {
    return "jdbc:" + sqlName.toLowerCase() + "://" + host + ":" + port + "/" + dbName;
  }
}
