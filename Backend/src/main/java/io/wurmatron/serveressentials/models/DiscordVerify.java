/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2025 Wurmcraft
 */
package io.wurmatron.serveressentials.models;

public class DiscordVerify {

  public String token;
  public String uuid;
  public String username;
  public String discordID;
  public String discordUsername;

  public DiscordVerify(
      String token, String uuid, String username, String discordID, String discordUsername) {
    this.token = token;
    this.uuid = uuid;
    this.username = username;
    this.discordID = discordID;
    this.discordUsername = discordUsername;
  }
}
