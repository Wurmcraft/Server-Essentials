/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.models.account;

import io.wurmatron.serveressentials.sql.SQLJson;

public class ServerTime implements SQLJson {
  public String serverID;
  public long totalTime;
  public long lastSeen;

  /**
   * @param serverID serverID that this related to
   * @param totalTime total time in minutes
   * @param lastSeen unix timestmap, for when the user was last seen on this id
   */
  public ServerTime(String serverID, long totalTime, long lastSeen) {
    this.serverID = serverID;
    this.totalTime = totalTime;
    this.lastSeen = lastSeen;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ServerTime)) return false;
    ServerTime that = (ServerTime) o;
    return totalTime == that.totalTime
        && lastSeen == that.lastSeen
        && serverID.equals(that.serverID);
  }
}
