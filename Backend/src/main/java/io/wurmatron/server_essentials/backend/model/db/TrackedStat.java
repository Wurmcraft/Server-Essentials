/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "statistics")
public class TrackedStat {

  @Column(name = "server_id")
  private String serverID;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "timestamp")
  private String timestamp;

  @Column(name = "event_type")
  private String eventType;

  @Column(name = "event_data")
  private String eventData;

  public TrackedStat() {}

  public TrackedStat(
      String serverID, String uuid, String timestamp, String eventType, String eventData) {
    this.serverID = serverID;
    this.uuid = uuid;
    this.timestamp = timestamp;
    this.eventType = eventType;
    this.eventData = eventData;
  }

  public String getServerID() {
    return serverID;
  }

  public void setServerID(String serverID) {
    this.serverID = serverID;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getEventData() {
    return eventData;
  }

  public void setEventData(String eventData) {
    this.eventData = eventData;
  }
}
