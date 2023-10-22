/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models;

public class ServerAuth {

  public String server_id;
  public String token;
  public String key;
  public String ip;

  public ServerAuth(String serverID, String token, String key, String ip) {
    this.server_id = serverID;
    this.token = token;
    this.key = key;
    this.ip = ip;
  }
}
