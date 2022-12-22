/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models.transfer;

import static com.wurmcraft.serveressentials.ServerEssentials.stackConverter;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.common.utils.DataWrapper;
import com.wurmcraft.serveressentials.common.utils.ItemStackConverter;
import java.util.Objects;
import net.minecraft.item.ItemStack;

public class ItemWrapper {

  public int count;
  public String item;
  public int meta;
  public String nbt;

  public ItemWrapper(String item) {
    if (stackConverter.isValid(item)) {
      DataWrapper itemWrapper = stackConverter.getName(item);
      this.item = itemWrapper.modid + itemWrapper.name;
      this.count = stackConverter.getDataSize(item);
      this.meta = stackConverter.getMeta(item);
      this.nbt = stackConverter.getExtraData(item);
    } else {
      this.item = null;
      this.count = 0;
      this.meta = -1;
      this.nbt = null;
    }
  }

  public ItemWrapper(int count, String item, int meta, String nbt) {
    this.count = count;
    this.item = item;
    this.meta = meta;
    this.nbt = nbt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ItemWrapper)) {
      return false;
    }
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
