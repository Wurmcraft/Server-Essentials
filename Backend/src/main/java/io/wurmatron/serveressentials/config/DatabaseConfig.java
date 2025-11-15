/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.config;

public class DatabaseConfig {

  public String username;
  public String password;
  public int port;
  public String host;
  public String database;
  public String sqlParams;
  public String connector;

  public DatabaseConfig(
      String username,
      String password,
      int port,
      String host,
      String database,
      String sqlParams,
      String connector) {
    this.username = username;
    this.password = password;
    this.port = port;
    this.host = host;
    this.database = database;
    this.sqlParams = sqlParams;
    this.connector = connector;
  }

  public DatabaseConfig() {
    this.username = "se-modded";
    this.password = "";
    this.port = 3306;
    this.host = "localhost";
    this.database = "server-essentials";
    this.sqlParams = "useSSL=false";
    this.connector = "mysql";
  }
}
