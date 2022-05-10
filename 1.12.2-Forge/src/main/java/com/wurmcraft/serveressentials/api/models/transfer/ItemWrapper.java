/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models.transfer;


import com.wurmcraft.serveressentials.ServerEssentials;

import java.util.Objects;

public class ItemWrapper  {

  public int count;
  public String item;
  public int meta;
  public String nbt;

  public ItemWrapper(String item) {
    // TODO Item Converter
    this.item = item;
    this.count = 0;
    this.meta = 0;
    this.nbt = null;
  }

  public ItemWrapper(int count, String item, int meta, String nbt) {
    this.count = count;
    this.item = item;
    this.meta = meta;
    this.nbt = nbt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemWrapper)) return false;
    ItemWrapper that = (ItemWrapper) o;
    return count == that.count
        && meta == that.meta
        && item.equals(that.item)
        && Objects.equals(nbt, that.nbt);
  }

  @Override
  public ItemWrapper clone() {
    String json = ServerEssentials.GSON.toJson(this);
    return ServerEssentials.GSON.fromJson(json, ItemWrapper.class);
  }
}
