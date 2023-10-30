package com.wurmcraft.serveressentials.common.modules.economy;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.models.MarketEntry;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.UsernameCache;
import org.apache.commons.io.FileUtils;

public class MarketHelper {

  public static List<MarketEntry> entries = null;

  public static List<MarketEntry> getEntries(boolean global, String filter) {
    if (entries == null) {
      entries = loadLocalEntries();
    }
    if (filter.equalsIgnoreCase("*")) {
      if (global) {
        return new ArrayList<>();
      } else {
        return entries;
      }
    } else {
      // TODO Apply Filter
      return new ArrayList<>();
    }
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

  public static List<MarketEntry> loadLocalEntries() {
    File marketFile = new File(ConfigLoader.SAVE_DIR + File.separator + "market.json");
    if (marketFile.exists()) {
      try {
        String file = FileUtils.readFileToString(marketFile, Charset.defaultCharset());
        MarketEntry[] entries = ServerEssentials.GSON.fromJson(file, MarketEntry[].class);
        return new ArrayList<>(Arrays.asList(entries));
      } catch (Exception e) {
        ServerEssentials.LOG.error("Failed to read market.json!");
        e.printStackTrace();
      }
    } else {
      try {
        Files.write(marketFile.toPath(), "".getBytes(), StandardOpenOption.WRITE,
            StandardOpenOption.CREATE_NEW);
      } catch (Exception e) {
        ServerEssentials.LOG.error("Failed to create market.json!");
        e.printStackTrace();
      }
    }
    return new ArrayList<>();
  }
}
