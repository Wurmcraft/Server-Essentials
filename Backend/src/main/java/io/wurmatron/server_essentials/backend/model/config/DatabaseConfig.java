/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.config;

import io.wurmatron.server_essentials.backend.config.Config;

public class DatabaseConfig implements Config {

  public String type;
  public String host;
  public String port;
  public String database;
  public String username;
  public String password;

  public DatabaseConfig(
      String type, String host, String port, String database, String username, String password) {
    this.type = type;
    this.host = host;
    this.port = port;
    this.database = database;
    this.username = username;
    this.password = password;
  }

  public DatabaseConfig() {
    this.type = "";
    this.host = "";
    this.port = "";
    this.database = "";
    this.username = "";
    this.password = "";
  }

  @Override
  public String getName() {
    return "database";
  }

  @Override
  public ConfigStyle getConfigStyle() {
    return ConfigStyle.TXT;
  }

  @Override
  public void setValues(boolean isReloaded) {
    // TODO Implement
  }
}
