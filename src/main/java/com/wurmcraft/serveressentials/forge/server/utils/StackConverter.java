package com.wurmcraft.serveressentials.forge.server.utils;

import com.wurmcraft.serveressentials.forge.api.data.DataWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class StackConverter {

  private StackConverter() {}

  public static int getMeta(String data) {
    if (data.contains("@[0-9]")) {
      boolean hasNBT = data.contains("^{");
      String meta =
          data.substring(data.indexOf('@') + 1, hasNBT ? data.indexOf('^') : data.indexOf('>'));
      try {
        int d = Integer.parseInt(meta);
        return d >= OreDictionary.WILDCARD_VALUE ? OreDictionary.WILDCARD_VALUE : d;
      } catch (NumberFormatException e) {
        return -1;
      }
    }
    return 0;
  }

  public static int getDataSize(String data) {
    if (data.contains(".*[0-9]x")) {
      String stackSize = data.substring(data.indexOf('<') + 1, data.indexOf('x'));
      try {
        return Integer.parseInt(stackSize);
      } catch (NumberFormatException e) {
        return -1;
      }
    }
    return 1;
  }

  public static DataWrapper getName(String data) {
    if (data.contains("<") && data.contains(">") && data.contains(":")) {
      boolean hasDedicatedStackSize = data.contains(".*[0-9]x");
      String modid =
          data.substring(
              hasDedicatedStackSize ? data.indexOf('x') + 1 : data.indexOf('<') + 1,
              data.indexOf(':'));
      boolean hasMETA = data.contains("@[0-9]");
      boolean hasNBT = data.contains("^{");
      String name;
      if (hasMETA) {
        name = data.substring(data.indexOf(':') + 1, data.indexOf('@'));
      } else if (hasNBT) {
        name = data.substring(data.indexOf(':') + 1, data.indexOf('^'));
      } else {
        name = data.substring(data.indexOf(':') + 1, data.indexOf('>'));
      }
      return new DataWrapper(modid, name);
    } else if (data.contains("<")
        && data.contains(">")
        && data.substring(data.indexOf('<') + 1, data.indexOf('>')).matches("<empty>")) {
      return new DataWrapper("empty", "empty");
    }
    return new DataWrapper("null", "null");
  }

  public static String getExtraData(String data) {
    if (data.contains("^")) {
      return data.substring(data.indexOf('^') + 1, data.indexOf('>'));
    }
    return "";
  }

  public static Item getBasicData(String data) {
    DataWrapper location = getName(data);
    if (location.getModid().equals("empty") && location.getName().equals("empty")) {
      return Items.AIR;
    }
    return ForgeRegistries.ITEMS.getValue(location.toResourceLocation());
  }

  public static ItemStack getData(String data) {
    if (data.substring(data.indexOf('<') + 1, data.indexOf('>')).matches("<empty>")) {
      return ItemStack.EMPTY;
    }
    return getItemStack(data);
  }

  private static ItemStack getItemStack(String data) {
    if (data.startsWith("<") && data.endsWith(">") && data.contains(":")) {
      if (data.substring(data.indexOf('<') + 1, data.indexOf('>')).matches("<empty>")) {
        return ItemStack.EMPTY;
      }
      Item item = getBasicData(data);
      int stackSize = getDataSize(data);
      int meta = getMeta(data);
      String nbt = getExtraData(data);
      ItemStack stack = new ItemStack(item, stackSize, meta);
      if (nbt.length() > 0) {
        try {
          NBTTagCompound stackNBT = JsonToNBT.getTagFromJson(nbt);
          stack.setTagCompound(stackNBT);
        } catch (NBTException e) {
          return ItemStack.EMPTY;
        }
      }
      return stack;
    }
    return null;
  }

  public static String toString(ItemStack data) {
    StringBuilder builder = new StringBuilder();
    builder.append("<");
    if (data.getCount() > 1) {
      builder.append(data.getCount());
      builder.append("x");
    }
    ResourceLocation location = data.getItem().getRegistryName();
    if (location != null) {
      builder.append(location.getResourceDomain());
      builder.append(":");
      builder.append(location.getResourcePath());
      if (data.getMetadata() > 0) {
        builder.append("@");
        builder.append(data.getMetadata());
      }
      if (data.hasTagCompound()) {
        builder.append("^");
        builder.append(data.getTagCompound() != null ? data.getTagCompound().toString() : "{}");
      }
      builder.append(">");
      return builder.toString();
    }
    return "";
  }
}