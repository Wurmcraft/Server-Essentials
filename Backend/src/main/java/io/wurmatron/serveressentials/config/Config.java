/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.config;

public class Config {

  public GeneralConfig general;
  public DatabaseConfig database;
  public ServerConfig server;
  public DiscordConfig discord;

  public Config(
      GeneralConfig general, DatabaseConfig database, ServerConfig server,
      DiscordConfig discord) {
    this.general = general;
    this.database = database;
    this.server = server;
    this.discord = discord;
  }

  public Config() {
    this.general = new GeneralConfig();
    this.database = new DatabaseConfig();
    this.server = new ServerConfig();
    this.discord = new DiscordConfig();
  }
}
