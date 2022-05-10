/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models;


import com.wurmcraft.serveressentials.ServerEssentials;

import java.util.Objects;

public class Donator {

  public String store;
  public String transaction_id;
  public Double amount;
  public String uuid;
  public Long timestamp;
  public String type;
  public String type_data;

  /**
   * @param store name of the store for this donation
   * @param transactionID id of the transaction from the given store
   * @param amount amount of the donation
   * @param uuid uuid of the person doing the donation
   * @param timestamp unix timestamp when the donation was received
   * @param type type of the donation, "Package", "Rank" etc...
   * @param typeData Full details about the donation
   */
  public Donator(
      String store,
      String transactionID,
      double amount,
      String uuid,
      long timestamp,
      String type,
      String typeData) {
    this.store = store;
    this.transaction_id = transactionID;
    this.amount = amount;
    this.uuid = uuid;
    this.timestamp = timestamp;
    this.type = type;
    this.type_data = typeData;
  }

  public Donator() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Donator)) return false;
    Donator donator = (Donator) o;
    return Objects.equals(store, donator.store)
        && Objects.equals(transaction_id, donator.transaction_id)
        && Objects.equals(amount, donator.amount)
        && Objects.equals(uuid, donator.uuid)
        && Objects.equals(timestamp, donator.timestamp)
        && Objects.equals(type, donator.type)
        && Objects.equals(type_data, donator.type_data);
  }

  @Override
  public Rank clone() {
    String json = ServerEssentials.GSON.toJson(this);
    return ServerEssentials.GSON.fromJson(json, Rank.class);
  }
}
