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
@Table(name = "world-logs")
public class LogEntry {

  @Column(name = "server_id")
  private String serverID;

  @Column(name = "timestamp")
  private String timestamp;

  @Column(name = "action_type")
  private String actionType;

  @Column(name = "action_data")
  private String actionData;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "x")
  private String x;

  @Column(name = "y")
  private String y;

  @Column(name = "z")
  private String z;

  @Column(name = "dim")
  private String dim;

  public LogEntry() {}

  public LogEntry(
      String serverID,
      String timestamp,
      String actionType,
      String actionData,
      String uuid,
      String x,
      String y,
      String z,
      String dim) {
    this.serverID = serverID;
    this.timestamp = timestamp;
    this.actionType = actionType;
    this.actionData = actionData;
    this.uuid = uuid;
    this.x = x;
    this.y = y;
    this.z = z;
    this.dim = dim;
  }

  public String getServerID() {
    return serverID;
  }

  public void setServerID(String serverID) {
    this.serverID = serverID;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public String getActionData() {
    return actionData;
  }

  public void setActionData(String actionData) {
    this.actionData = actionData;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getX() {
    return x;
  }

  public void setX(String x) {
    this.x = x;
  }

  public String getY() {
    return y;
  }

  public void setY(String y) {
    this.y = y;
  }

  public String getZ() {
    return z;
  }

  public void setZ(String z) {
    this.z = z;
  }

  public String getDim() {
    return dim;
  }

  public void setDim(String dim) {
    this.dim = dim;
  }
}
