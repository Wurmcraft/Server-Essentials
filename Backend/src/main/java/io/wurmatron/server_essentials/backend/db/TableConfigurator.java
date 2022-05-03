/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.db;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.io.FileUtils;
import java.io.IOException;
import java.sql.PreparedStatement;
import org.hibernate.Session;

public class TableConfigurator {

  public static final String[] TABLES =
      new String[] {
        "users",
        "ranks",
        "autoranks",
        "actions",
        "bans",
        "donators",
        "currencies",
        "world-logs",
        "markets",
        "statistics",
        "transfers"
      };

  /**
   * Creates a table based on to be configured table templates in sql/{jdbc}/{table_name}.sql
   *
   * @param dbType jdbc connector name of the database to create the table on
   * @param name name of the table file to be created, generally the table's name
   * @return if the table was created successfully
   */
  public static boolean createTable(String dbType, String name) {
    try {
      String sqlTableData =
          FileUtils.readInternalFile("sql\\" + dbType.toLowerCase() + "\\" + name + ".sql");
      if (sqlTableData.length() > 0) {
        if (DatabaseConnector.getSession() != null && !DatabaseConnector.getSession().isClosed()) {
          Session session = DatabaseConnector.getSession().getCurrentSession();
          session.beginTransaction();
          session.createSQLQuery(sqlTableData).executeUpdate();
          session.getTransaction().commit();
          return true;
        } else {
          ServerEssentialsBackend.LOG.warn(
              "Failed to send sql request due to no session existing!");
        }
      } else {
        ServerEssentialsBackend.LOG.warn(
            "SQL Table template for '"
                + name.toLowerCase()
                + "' is empty via "
                + dbType.toUpperCase());
      }
    } catch (IOException e) {
      e.printStackTrace();
      ServerEssentialsBackend.LOG.warn(
          "Failed to create table '" + name.toUpperCase() + "' via " + dbType.toUpperCase());
    }
    return false;
  }

  public static boolean tableExists(String name) {
    try {
      PreparedStatement statment =
          DatabaseConnector.getConnection().prepareStatement("SELECT COUNT(*) FROM " + name);
      statment.execute();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
