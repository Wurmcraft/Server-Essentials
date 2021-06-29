package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.models.transfer.ItemWrapper;

import java.util.Objects;

public class MarketEntry {
  public String serverID;
  public String sellerUUID;
  public ItemWrapper item;
  public String currencyName;
  public Double currencyAmount;
  public Long timestamp;
  public String marketType;
  public String marketData;
  public String transferID;

  /**
   * @param serverID id of the server, where the trade started
   * @param sellerUUID uuid of the seller
   * @param item json data of the item to be sold
   * @param currencyName name of the currency being used by the entry
   * @param currencyAmount current amount of the currency being used by this entry
   * @param timestamp unix timestamp when the entry was created
   * @param marketType type of the market for this entry
   * @param marketData data related to the market entry
   * @param transferID server's transferID, for use with multi-server market's
   */
  public MarketEntry(
      String serverID,
      String sellerUUID,
      ItemWrapper item,
      String currencyName,
      double currencyAmount,
      long timestamp,
      String marketType,
      String marketData,
      String transferID) {
    this.serverID = serverID;
    this.sellerUUID = sellerUUID;
    this.item = item;
    this.currencyName = currencyName;
    this.currencyAmount = currencyAmount;
    this.timestamp = timestamp;
    this.marketType = marketType;
    this.marketData = marketData;
    this.transferID = transferID;
  }

  public MarketEntry() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MarketEntry)) return false;
    MarketEntry that = (MarketEntry) o;
    return Objects.equals(serverID, that.serverID)
        && Objects.equals(sellerUUID, that.sellerUUID)
        && Objects.equals(item, that.item)
        && Objects.equals(currencyName, that.currencyName)
        && Objects.equals(currencyAmount, that.currencyAmount)
        && Objects.equals(timestamp, that.timestamp)
        && Objects.equals(marketType, that.marketType)
        && Objects.equals(marketData, that.marketData)
        && Objects.equals(transferID, that.transferID);
  }
}
