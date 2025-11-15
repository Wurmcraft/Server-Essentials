/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2025 Wurmcraft
 */
package io.wurmatron.serveressentials.models.data_wrapper;

public class DiscordVerified {

  public String discordID;
  public String discordName;
  public String uuid;
  public String username;

  public DiscordVerified(String discordID, String discordName, String uuid, String username) {
    this.discordID = discordID;
    this.discordName = discordName;
    this.uuid = uuid;
    this.username = username;
  }
}
