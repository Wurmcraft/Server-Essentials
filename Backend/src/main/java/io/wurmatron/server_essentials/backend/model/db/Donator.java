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
@Table(name = "donators")
public class Donator {

  @Column(name = "store")
  private String store;

  @Column(name = "transaction_id")
  private String transactionID;

  @Column(name = "amount")
  private String amount;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "timestamp")
  private String timestamp;

  @Column(name = "type")
  private String type;

  @Column(name = "type_data")
  private String type_data;

  public Donator() {}

  public Donator(
      String store,
      String transactionID,
      String amount,
      String uuid,
      String timestamp,
      String type,
      String type_data) {
    this.store = store;
    this.transactionID = transactionID;
    this.amount = amount;
    this.uuid = uuid;
    this.timestamp = timestamp;
    this.type = type;
    this.type_data = type_data;
  }

  public String getStore() {
    return store;
  }

  public void setStore(String store) {
    this.store = store;
  }

  public String getTransactionID() {
    return transactionID;
  }

  public void setTransactionID(String transactionID) {
    this.transactionID = transactionID;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType_data() {
    return type_data;
  }

  public void setType_data(String type_data) {
    this.type_data = type_data;
  }
}
