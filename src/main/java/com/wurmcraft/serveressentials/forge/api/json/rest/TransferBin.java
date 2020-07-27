package com.wurmcraft.serveressentials.forge.api.json.rest;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

/**
 * Transfer Items between servers
 */
public class TransferBin implements JsonParser {

  public String uuid;
  public ItemBin[] transfers;

  public TransferBin(String uuid) {
    this.uuid = uuid;
    this.transfers = new ItemBin[0];
  }

  public TransferBin(String uuid,
      ItemBin[] transfers) {
    this.uuid = uuid;
    this.transfers = transfers;
  }

  public static class ItemBin {

    public String transferID;
    public String[] items;

    public ItemBin(String transferID) {
      this.transferID = transferID;
      this.items = new String[0];
    }

    public ItemBin(String transferID, String[] items) {
      this.transferID = transferID;
      this.items = items;
    }
  }

  @Override
  public String getID() {
    return uuid;
  }
}
