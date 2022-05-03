/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transfers")
public class TransferEntry {

  @Id
  @Column(name = "transfer_id")
  public String transferID;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "start_time")
  private String startTime;

  @Column(name = "items")
  private String items;

  @Column(name = "server_id")
  private String serverID;

  @Column(name = "transfer_data")
  private String transferData;

  public TransferEntry() {}

  public TransferEntry(
      String transferID,
      String uuid,
      String startTime,
      String items,
      String serverID,
      String transferData) {
    this.transferID = transferID;
    this.uuid = uuid;
    this.startTime = startTime;
    this.items = items;
    this.serverID = serverID;
    this.transferData = transferData;
  }

  public String getTransferID() {
    return transferID;
  }

  public void setTransferID(String transferID) {
    this.transferID = transferID;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getItems() {
    return items;
  }

  public void setItems(String items) {
    this.items = items;
  }

  public String getServerID() {
    return serverID;
  }

  public void setServerID(String serverID) {
    this.serverID = serverID;
  }

  public String getTransferData() {
    return transferData;
  }

  public void setTransferData(String transferData) {
    this.transferData = transferData;
  }
}
