/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.ServerEssentials;
import java.util.Objects;

public class TrackedStat {

  public String server_id;
  public String uuid;
  public long timestamp;
  public String event_type;
  public String event_data;

  /**
   * @param serverID id of the server where this occurred
   * @param uuid uuid of the person for the event
   * @param timestamp unix timestamp for this event
   * @param eventType type of event that has occurred
   * @param eventData data related to the event
   */
  public TrackedStat(
      String serverID, String uuid, long timestamp, String eventType, String eventData) {
    this.server_id = serverID;
    this.uuid = uuid;
    this.timestamp = timestamp;
    this.event_type = eventType;
    this.event_data = eventData;
  }

  public TrackedStat() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TrackedStat)) return false;
    TrackedStat that = (TrackedStat) o;
    return timestamp == that.timestamp
        && Objects.equals(server_id, that.server_id)
        && Objects.equals(uuid, that.uuid)
        && Objects.equals(event_type, that.event_type)
        && Objects.equals(event_data, that.event_data);
  }

  @Override
  public TrackedStat clone() {
    String json = ServerEssentials.GSON.toJson(this);
    return ServerEssentials.GSON.fromJson(json, TrackedStat.class);
  }
}
