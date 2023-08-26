/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.ServerEssentials;

public class Action {

  public String related_id;
  public String host;
  public String action;
  public String action_data;
  public String timestamp;

  /**
   * @param relatedID serverID/discord Channel ID
   * @param host "Minecraft", "Discord"
   * @param action Name of the given action, that has happened
   * @param actionData Json data related to the given action
   * @param timestamp Unix Timestamp for when the action occurred
   */
  public Action(String relatedID, String host, String action, String actionData,
      String timestamp) {
    this.related_id = relatedID;
    this.host = host;
    this.action = action;
    this.action_data = actionData;
    this.timestamp = timestamp;
  }

  public Action() {
  }

  @Override
  public String toString() {
    return ServerEssentials.GSON.toJson(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Action)) {
      return false;
    }
    Action other = (Action) o;
    return related_id.equals(other.related_id)
        && host.equals(other.host)
        && action.equals(other.action)
        && timestamp.equals(other.timestamp);
  }

  @Override
  public Action clone() {
    String json = ServerEssentials.GSON.toJson(this);
    return ServerEssentials.GSON.fromJson(json, Action.class);
  }
}
