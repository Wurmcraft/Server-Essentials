package com.wurmcraft.serveressentials.common.modules.economy;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.models.MarketEntry;
import com.wurmcraft.serveressentials.api.models.transfer.ItemWrapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.UsernameCache;

public class MarketHelper {

  // TODO Temp
  public static List<MarketEntry> getEntries(boolean global, String filter) {
    List<MarketEntry> entries = new ArrayList<>();
    entries.add(
        new MarketEntry("This", "aad", new ItemWrapper(12, "<cooked_porkchop>", 0, ""),
            "Default", 500,
            Instant.now().toEpochMilli(), "Buy", "{}", ""));
    return entries;
  }

  public static ItemStack getStackForMarketEntry(MarketEntry entry) {
    ItemStack stack = ServerEssentials.stackConverter.getData(entry.item.item);
    stack.setCount(entry.item.count);
    stack.setItemDamage(entry.item.meta);
    if (entry.item.nbt != null && !entry.item.nbt.isEmpty()) {
      try {
        stack.setTagCompound(JsonToNBT.getTagFromJson(entry.item.nbt));
      } catch (Exception e) {

      }
    }
    return stack;
  }

  // TODO Lang
  public static ItemStack getShopDisplayItem(MarketEntry entry, boolean global) {
    ItemStack item = getStackForMarketEntry(entry);
    addTooltip(item, "Cost: " + entry.currency_amount);
    try {
      addTooltip(item, "Seller: " + UsernameCache.getLastKnownUsername(
          UUID.fromString(entry.seller_uuid)));
    } catch (Exception e) {
      item = addTooltip(item, "Seller: <Error>");
    }
    if (global) {
      addTooltip(item, "Server: " + entry.server_id);
    }
    return item;
  }

  public static ItemStack addTooltip(ItemStack stack, String msg) {
    NBTTagCompound displayTag = stack.getOrCreateSubCompound("display");
    if (!displayTag.hasKey("Lore")) {
      displayTag.setTag("Lore", new NBTTagList());
    }
    NBTTagList lore = displayTag.getTagList("Lore", 9);
    lore.appendTag(new NBTTagString(msg));  // FIX Append not appending, damn mc
    return stack;
  }
}
