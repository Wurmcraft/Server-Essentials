/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.ServerEssentialsRest.config;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabasePopulator {

  public static String[] tables = {
    "actions",
    "autoranks",
    "bans",
    "currencys",
    "donator",
    "logging",
    "markets",
    "ranks",
    "statistics",
    "transfers",
    "users"
  };

  /** Checks if all the tables exist within the sql database if not they are created */
  public static void setupDB(Connection c) {
    for (String table : tables) {
      if (!checkIfExists(c, table)) {
        LOG.info("SQL Table '{}' does not exist!, Creating... ", table);
        createTable(c, table);
      }
    }
    for (String tableName : tables)
      checkAndUpdateTable(c, tableName);
  }

  private static void checkAndUpdateTable(Connection c, String tableName) {
    try {
      DatabaseMetaData metaData = c.getMetaData();
      ResultSet columns = metaData.getColumns(null, null, tableName, null);
      Map<String, String> existingCollums = new LinkedHashMap<>();
      while (columns.next())
        existingCollums.put(columns.getString("COLUMN_NAME"), columns.getString("TYPE_NAME"));

      // Get Columns that the table should have
      String expCollums = readSQLSetupFile(tableName + ".sql");
      Map<String, String> expectedColumnMap = parseSQLColumns(expCollums);

      for (Map.Entry<String, String> entry : expectedColumnMap.entrySet()) {
        if (!existingCollums.containsKey(entry.getKey())) {
          executeSql(c, "ALTER TABLE " + tableName + " ADD COLUMN " + entry.getKey() + " " + entry.getValue());
          LOG.info("Added new column '{}' to table '{}'", entry.getKey(), tableName);
        }
      }
    } catch (Exception e) {
      LOG.error("Failed to check and update schema for table '{}'", tableName, e);
    }
  }

  private static Map<String, String> parseSQLColumns(String sql) {
    Map<String, String> columns = new LinkedHashMap<>();
    String[] lines = sql.split("\n");
    for (String line : lines) {
      if (line.trim().startsWith("CREATE TABLE"))
        continue;
      if (line.trim().startsWith(")") || line.trim().isEmpty())
        continue;
      String[] parts = line.split(" ");
      if (parts.length == 2)
        columns.put(parts[0], parts[1]);
    }
    return columns;
  }

  private static void executeSql(Connection c, String sql) {
    try {
      PreparedStatement statement = c.prepareStatement(sql);
      statement.executeUpdate();
    } catch (Exception e) {
      LOG.error("Failed to execute SQL statement: {}", sql, e);
    }
  }

  /**
   * Check if a table exists
   *
   * @param tableName name of the table to check if exists
   * @return if the table exists
   */
  private static boolean checkIfExists(Connection c, String tableName) {
    try {
      String sqlStatment = "";
      if (config.database.connector.equalsIgnoreCase("mysql")) {
        sqlStatment = "SELECT * FROM information_schema.tables WHERE table_name=? AND table_schema=? LIMIT 1";
      } else if (config.database.connector.equalsIgnoreCase("postgresql")) {
        sqlStatment = "SELECT * FROM information_schema.tables WHERE table_name=? AND table_schema='public' LIMIT 1";
      }
      PreparedStatement sql = c.prepareStatement(sqlStatment);
      sql.setString(1, tableName);
      sql.setString(2, ServerEssentialsRest.config.database.database);
      if (!sql.executeQuery().next()) {
        return false;
      }
    } catch (Exception e) {
      LOG.debug("Failed to check if table exists ' {}' ({})", tableName, e.getLocalizedMessage());
    }
    return true;
  }

  /**
   * Creates a table based on its name, used to lookup setup sql file
   *
   * @param tableName name of the table to be created
   */
  public static void createTable(Connection c, String tableName) {
    String tableSQL =
        readSQLSetupFile(
            "sql"
                + File.separator
                + config.database.connector
                + File.separator
                + tableName
                + ".sql");
    try {
      c.createStatement().execute(tableSQL);
      LOG.info("Table '{}' Created!", tableName);
    } catch (Exception e) {
      LOG.warn("Failed to create table '{}' ({})", tableName, e.getLocalizedMessage());
    }
  }

  /**
   * Reads the provided resource into a string
   *
   * @param fileName name of the file to read
   * @return the file read into a single string
   */
  private static String readSQLSetupFile(String fileName) {
    InputStream in = DatabasePopulator.class.getClassLoader().getResourceAsStream(fileName);
    if (in != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      return reader.lines().collect(Collectors.joining());
    }
    return "";
  }
}
