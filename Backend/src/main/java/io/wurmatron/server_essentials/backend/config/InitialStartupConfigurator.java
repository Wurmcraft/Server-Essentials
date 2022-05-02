/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.config;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.db.DatabaseConnector;
import io.wurmatron.server_essentials.backend.db.TableConfigurator;
import java.io.IOException;

public class InitialStartupConfigurator {

  public static boolean doDatabaseTablesExist() {
    for (String table : TableConfigurator.TABLES) {
      if (!TableConfigurator.tableExists(table)) {
        return false;
      }
    }
    return true;
  }

  private static boolean createTables(String dbType) {
    for (String table : TableConfigurator.TABLES) {
      if (!TableConfigurator.createTable(dbType, table)) {
        return false;
      }
    }
    return true;
  }

  public static boolean loadOrSetup(String[] args) {
    try {
      ServerEssentialsBackend.backendConfiguration =
          ConfigLoader.loadOrCreateBackendConfig(ServerEssentialsBackend.SAVE_DIR);
    } catch (IOException e) {
      ServerEssentialsBackend.LOG.warn(e.getMessage());
      ServerEssentialsBackend.LOG.info("Failed to load configuration file");
      return false;
    }
    // Load and configure database
    DatabaseConnector.loadDBConfig(false);
    if (!doDatabaseTablesExist()) {
      if (!createTables(DatabaseConnector.databaseType)) {
        return false;
      }
    }
    return true;
  }
}
