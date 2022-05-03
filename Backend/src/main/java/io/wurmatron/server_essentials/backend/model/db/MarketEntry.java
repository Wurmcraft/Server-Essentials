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
@Table(name = "markets")
public class MarketEntry {

  @Column(name = "server_id")
  private String serverID;

  @Column(name = "seller_uuid")
  private String sellerUUID;

  @Column(name = "item")
  private String item;

  @Column(name = "currency")
  private String currency;

  @Column(name = "timestamp")
  private String timestamp;

  @Column(name = "market_type")
  private String marketType;

  @Column(name = "market_data")
  private String marketData;

  @Column(name = "transfer_id")
  private String transferID;

  public MarketEntry() {}

  public MarketEntry(
      String serverID,
      String sellerUUID,
      String item,
      String currency,
      String timestamp,
      String marketType,
      String marketData,
      String transferID) {
    this.serverID = serverID;
    this.sellerUUID = sellerUUID;
    this.item = item;
    this.currency = currency;
    this.timestamp = timestamp;
    this.marketType = marketType;
    this.marketData = marketData;
    this.transferID = transferID;
  }

  public String getServerID() {
    return serverID;
  }

  public void setServerID(String serverID) {
    this.serverID = serverID;
  }

  public String getSellerUUID() {
    return sellerUUID;
  }

  public void setSellerUUID(String sellerUUID) {
    this.sellerUUID = sellerUUID;
  }

  public String getItem() {
    return item;
  }

  public void setItem(String item) {
    this.item = item;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getMarketType() {
    return marketType;
  }

  public void setMarketType(String marketType) {
    this.marketType = marketType;
  }

  public String getMarketData() {
    return marketData;
  }

  public void setMarketData(String marketData) {
    this.marketData = marketData;
  }

  public String getTransferID() {
    return transferID;
  }

  public void setTransferID(String transferID) {
    this.transferID = transferID;
  }
}
