/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models;


import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.models.transfer.ItemWrapper;

import java.util.Arrays;

public class TransferEntry {

  public Long transfer_id;
  public String uuid;
  public Long start_time;
  public ItemWrapper[] items;
  public String server_id;

  /**
   * @param transferID id of the given transfer (Don't change as this is used internally to track)
   * @param uuid uuid of the person that started the item transfer
   * @param startTime time the transfer was started, (in case of timeouts)
   * @param items list of items to be transferred (in json format)
   * @param serverID id of the server that the transfer was started on
   */
  public TransferEntry(
      long transferID, String uuid, long startTime, ItemWrapper[] items, String serverID) {
    this.transfer_id = transferID;
    this.uuid = uuid;
    this.start_time = startTime;
    this.items = items;
    this.server_id = serverID;
  }

  public TransferEntry() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TransferEntry)) return false;
    TransferEntry that = (TransferEntry) o;
    return transfer_id.equals(that.transfer_id)
        && start_time.equals(that.start_time)
        && uuid.equals(that.uuid)
        && Arrays.equals(that.items, items)
        && server_id.equals(that.server_id);
  }

  @Override
  public TransferEntry clone() {
    String json = ServerEssentials.GSON.toJson(this);
    return ServerEssentials.GSON.fromJson(json, TransferEntry.class);
  }
}
