/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.models;

public class ServerStatus {

  public String serverID;
  public Long delayMS;
  public Long lastUpdate;
  public String[] onlinePlayers;
  public String[] playerInfo;
  public String currentState;
  public String specialData;

  public ServerStatus() {
  }

  public ServerStatus(
      String serverID,
      Long delayMS,
      Long lastUpdate,
      String[] onlinePlayers,
      String[] playerInfo,
      String currentState,
      String specialData) {
    this.serverID = serverID;
    this.delayMS = delayMS;
    this.lastUpdate = lastUpdate;
    this.onlinePlayers = onlinePlayers;
    this.playerInfo = playerInfo;
    this.currentState = currentState;
    this.specialData = specialData;
  }
}
