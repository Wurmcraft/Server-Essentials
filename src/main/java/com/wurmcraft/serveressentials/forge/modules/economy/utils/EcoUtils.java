package com.wurmcraft.serveressentials.forge.modules.economy.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.CurrencyConversion;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

public class EcoUtils {


  public static void createDefaultConversions() {
    CurrencyConversion defaultConversion = new CurrencyConversion("Default", 1);
    SECore.dataHandler.registerData(DataKey.CURRENCY, defaultConversion);
  }

  public static void updateConversion(CurrencyConversion conversion) {
    SECore.dataHandler.registerData(DataKey.CURRENCY, conversion);
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      RestRequestHandler.Economy.overrideCurrency(conversion);
    }
  }

  public static void checkAndLoadEconomy() {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      CurrencyConversion[] conversions = RestRequestHandler.Economy.getAllCurrencies();
      CurrencyConversion[] fileConversions = SECore.dataHandler
          .getDataFromKey(DataKey.CURRENCY, new CurrencyConversion()).values()
          .toArray(new CurrencyConversion[0]);
      if (conversions != null && conversions.length > 0) {
        ServerEssentialsServer.isUpdateInProgress = true;
        for (CurrencyConversion fileRank : fileConversions) {
          SECore.dataHandler.delData(DataKey.CURRENCY, fileRank.getID(), true);
        }
        ServerEssentialsServer.isUpdateInProgress = false;
        for (CurrencyConversion r : conversions) {
          SECore.dataHandler.registerData(DataKey.CURRENCY, r);
        }
      } else {
        createDefaultConversions();
      }
    } else {
      CurrencyConversion[] conversion = SECore.dataHandler
          .getDataFromKey(DataKey.CURRENCY, new CurrencyConversion()).values()
          .toArray(new CurrencyConversion[0]);
      if (conversion.length <= 0) {
        createDefaultConversions();
      }
    }
  }

  public static boolean hasCurrency(EntityPlayer player, String name, double amount) {
    StoredPlayer playerData = (StoredPlayer) SECore.dataHandler
        .getData(DataKey.PLAYER, player.getGameProfile().getId().toString());
    for (Currency coin : playerData.global.wallet.currency) {
      if (coin.name.equalsIgnoreCase(name)) {
        return (coin.amount - amount) >= 0;
      }
    }
    return false;
  }

  public static boolean hasCurrency(EntityPlayer player, double amount) {
    return hasCurrency(player, EconomyModule.config.defaultCurrency.name, amount);
  }

  public static void consumeCurrency(EntityPlayer player, String name, double cost) {
    StoredPlayer playerData = (StoredPlayer) SECore.dataHandler
        .getData(DataKey.PLAYER, player.getGameProfile().getId().toString());
    for (Currency coin : playerData.global.wallet.currency) {
      if (coin.name.equalsIgnoreCase(name)) {
        coin.amount = (long) (coin.amount - cost);
      }
    }
    SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      RestRequestHandler.User.overridePlayer(playerData.uuid, playerData.global);
    }
  }

  public static void consumeCurrency(EntityPlayer player, double cost) {
    consumeCurrency(player, EconomyModule.config.defaultCurrency.name, cost);
  }
}
