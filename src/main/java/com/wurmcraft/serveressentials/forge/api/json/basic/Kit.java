package com.wurmcraft.serveressentials.forge.api.json.basic;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.server.utils.StackConverter;
import java.util.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class Kit implements JsonParser {

  public String name;
  public int waitTime;
  public KitInvenntory invenntory;

  public Kit(String name, int waitTime,
      KitInvenntory invenntory) {
    this.name = name;
    this.waitTime = waitTime;
    this.invenntory = invenntory;
  }

  public Kit(String name, int waitTime,
      InventoryPlayer inv) {
    this.name = name;
    this.waitTime = waitTime;
    this.invenntory = new KitInvenntory(inv.mainInventory, inv.armorInventory);
  }

  @Override
  public String getID() {
    return name;
  }

  public static class KitInvenntory {

    public List<String> main;
    public List<String> armor;

    public KitInvenntory(List<String> main, List<String> armor) {
      this.main = main;
      this.armor = armor;
    }

    public KitInvenntory(NonNullList<ItemStack> mainInv,
        NonNullList<ItemStack> armorInv) {
      main = new ArrayList<>();
      for (ItemStack m : mainInv) {
        main.add(StackConverter.toString(m));
      }
      armor = new ArrayList<>();
      for (ItemStack m : armorInv) {
        armor.add(StackConverter.toString(m));
      }
    }
  }
}
