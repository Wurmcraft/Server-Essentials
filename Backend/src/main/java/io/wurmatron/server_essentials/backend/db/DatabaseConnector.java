/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.db;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.cli.CLIParser;
import io.wurmatron.server_essentials.backend.config.ConfigLoader;
import io.wurmatron.server_essentials.backend.model.config.DatabaseConfig;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import joptsimple.internal.Strings;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class DatabaseConnector {

  public static String databaseType;
  static SessionFactory sessionFactory;

  /** Get the current active session for the current selected database */
  public static SessionFactory getSession() {
    if (sessionFactory != null && sessionFactory.isOpen()) {
      return sessionFactory;
    } else if (sessionFactory != null) {
      sessionFactory.openSession();
      return sessionFactory;
    }
    return null;
  }

  public static Connection getConnection() throws SQLException {
    Session session = getSession().getCurrentSession();
    SessionFactoryImplementor sessionFactoryImplementation =
        (SessionFactoryImplementor) session.getSessionFactory();
    return sessionFactoryImplementation
        .getJdbcServices()
        .getBootstrapJdbcConnectionAccess()
        .obtainConnection();
  }

  public static void loadDBConfig(boolean resetConfig) {
    HashMap<String, String> dbSetup = new HashMap<>();
    File dbConfigFile =
        new File(ServerEssentialsBackend.SAVE_DIR + File.separator + "database.txt");
    // Create new db config file
    if (resetConfig || !dbConfigFile.exists()) {
      dbSetup = askDBConfigValues();
      DatabaseConfig dbConfig =
          new DatabaseConfig(
              dbSetup.get("sql_name"),
              dbSetup.get("sql_host"),
              dbSetup.get("sql_port"),
              dbSetup.get("sql_table"),
              dbSetup.get("sql_username"),
              dbSetup.get("sql_password"));
      try {
        if (dbConfigFile.exists()) {
          ConfigLoader.save(dbConfig, ServerEssentialsBackend.SAVE_DIR);
        } else {
          ConfigLoader.create(dbConfig, ServerEssentialsBackend.SAVE_DIR);
        }
      } catch (IOException e) {
        ServerEssentialsBackend.LOG.error(
            "Failed to create '" + dbConfigFile.getAbsolutePath() + "'");
        ServerEssentialsBackend.LOG.error(e.getMessage());
        return;
      }
    }
    // Load db File
    try {
      DatabaseConfig config =
          ConfigLoader.load(new DatabaseConfig(), ServerEssentialsBackend.SAVE_DIR);
      dbSetup.put("sql_name", config.type);
      dbSetup.put("sql_host", config.host);
      dbSetup.put("sql_port", config.port);
      dbSetup.put("sql_table", config.database);
      dbSetup.put("sql_username", config.username);
      dbSetup.put("sql_password", config.password);
    } catch (IOException e) {
      ServerEssentialsBackend.LOG.error("Failed to load '" + dbConfigFile.getAbsolutePath() + "'");
      ServerEssentialsBackend.LOG.error(e.getMessage());
    }

    DatabaseConfigurator.initialDBSetup(dbSetup);
  }

  public static HashMap<String, String> askDBConfigValues() {
    HashMap<String, String> dbSetup = new HashMap<>();
    String sqlName = "";
    while (!DatabaseConfigurator.driverClasses.containsKey(sqlName.toLowerCase())) {
      sqlName = CLIParser.getUserInput("SQL Database Type");
      if (!DatabaseConfigurator.driverClasses.containsKey(sqlName.toLowerCase())) {
        ServerEssentialsBackend.LOG.warn("Invalid Database type (" + sqlName.toLowerCase() + ")");
        ServerEssentialsBackend.LOG.info(
            "Valid database types include: ("
                + Strings.join(DatabaseConfigurator.driverClasses.keySet(), ", ")
                + ")");
      }
    }
    String sqlHost = CLIParser.getUserInput("Host");
    String sqlPort = CLIParser.getUserInput("Port");
    String sqlTable = CLIParser.getUserInput("Database");
    String sqlUsername = CLIParser.getUserInput("Username");
    String sqlPassword = CLIParser.getUserInput("Password"); // TODO Hide password input
    dbSetup.put("sql_name", sqlName.toLowerCase());
    dbSetup.put("sql_host", sqlHost);
    dbSetup.put("sql_port", sqlPort);
    dbSetup.put("sql_table", sqlTable);
    dbSetup.put("sql_username", sqlUsername);
    dbSetup.put("sql_password", sqlPassword);
    return dbSetup;
  }
}
