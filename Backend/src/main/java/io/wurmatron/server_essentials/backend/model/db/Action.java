/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity()
@Table(name = "actions")
public class Action {

  @Column(name = "related_id")
  private String relatedID;

  @Column(name = "host")
  public String host;

  @Column(name = "action")
  public String action;

  @Column(name = "action_data")
  public String actionData;

  @Column(name = "timestamp")
  public String timestamp;

  public Action() {}

  public Action(String relatedID, String host, String action, String actionData, String timestamp) {
    this.relatedID = relatedID;
    this.host = host;
    this.action = action;
    this.actionData = actionData;
    this.timestamp = timestamp;
  }

  public String getRelatedID() {
    return relatedID;
  }

  public void setRelatedID(String relatedID) {
    this.relatedID = relatedID;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getActionData() {
    return actionData;
  }

  public void setActionData(String actionData) {
    this.actionData = actionData;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
