package com.wurmcraft.serveressentials.common.utils;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.*;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.FileDataLoader;

public class RestTransferUtils {

  public static boolean transferInProgress = false;
  public static int transfers = 0;

  public static void transferToRest() {
    transferInProgress = true;
    ServerEssentials.LOG.warn(
        "Starting data transfer... (Server will not be join-able until this is complete!");
    FileDataLoader fileHandler = new FileDataLoader();
    FileDataLoader.SAVE_FOLDER = "Storage";
    try {
      transferAutoRanks(fileHandler);
      transferCurrency(fileHandler);
      transferDonator(fileHandler);
      transferMarket(fileHandler);
      transferRank(fileHandler);
      transferAccount(fileHandler);
    } catch (Exception e) {
      e.printStackTrace();
    }
    ServerEssentials.LOG.warn("Data Transfer complete {} items transferred", transfers);
    FileDataLoader.SAVE_FOLDER = "Cache";
    transferInProgress = false;
  }

  private static void transferAutoRanks(FileDataLoader oldData) {
    ServerEssentials.LOG.info("Starting Transfer of AutoRanks");
    if (oldData.getFromKey(DataLoader.DataType.AUTORANK, new AutoRank()) != null)
      for (AutoRank ar :
          oldData.getFromKey(DataLoader.DataType.AUTORANK, new AutoRank()).values()) {
        if (SECore.dataLoader.register(DataLoader.DataType.AUTORANK, ar.rank, ar)) {
          ServerEssentials.LOG.info("Transferred '{}' AutoRank", ar.rank);
          transfers++;
          if (oldData.delete(DataLoader.DataType.AUTORANK, ar.rank))
            ServerEssentials.LOG.debug("Deleting AutoRank '{}' (transferred)", ar.rank);
          else ServerEssentials.LOG.debug("Failed to delete AutoRank '{}' (transfer)", ar.rank);
        } else {
          ServerEssentials.LOG.warn(
              "AutoRank '{}' already exists. (To Prevent issues, merging will not be attempted)",
              ar.rank);
        }
      }
    ServerEssentials.LOG.info("Transfer of AutoRanks Complete.");
  }

  private static void transferCurrency(FileDataLoader oldData) {
    ServerEssentials.LOG.info("Starting Transfer of Currency");
    if (oldData.getFromKey(DataLoader.DataType.CURRENCY, new Currency()) != null)
      for (Currency curr :
          oldData.getFromKey(DataLoader.DataType.CURRENCY, new Currency()).values()) {
        if (SECore.dataLoader.register(DataLoader.DataType.CURRENCY, curr.display_name, curr)) {
          ServerEssentials.LOG.info("Transferred '{}' Currency", curr.display_name);
          transfers++;
          if (oldData.delete(DataLoader.DataType.CURRENCY, curr.display_name))
            ServerEssentials.LOG.debug("Deleting Currency '{}' (transferred)", curr.display_name);
          else
            ServerEssentials.LOG.debug(
                "Failed to delete Currency '{}' (transfer)", curr.display_name);
        } else {
          ServerEssentials.LOG.warn(
              "Currency '{}' already exists. (To Prevent issues, merging will not be attempted)",
              curr.display_name);
        }
      }
    ServerEssentials.LOG.info("Transfer of Currency Complete.");
  }

  private static void transferDonator(FileDataLoader oldData) {
    ServerEssentials.LOG.info("Starting Transfer of Donators");
    if (oldData.getFromKey(DataLoader.DataType.DONATOR, new Donator()) != null)
      for (Donator donator :
          oldData.getFromKey(DataLoader.DataType.DONATOR, new Donator()).values()) {
        if (SECore.dataLoader.register(DataLoader.DataType.DONATOR, donator.uuid, donator)) {
          ServerEssentials.LOG.info("Transferred '{}' Donator", donator.uuid);
          transfers++;
          if (oldData.delete(DataLoader.DataType.DONATOR, donator.uuid))
            ServerEssentials.LOG.debug("Deleting donator '{}' (transferred)", donator.uuid);
          else ServerEssentials.LOG.debug("Failed to delete Donator '{}' (transfer)", donator.uuid);
        } else {
          ServerEssentials.LOG.warn(
              "Donator '{}' already exists. (To Prevent issues, merging will not be attempted)",
              donator.uuid);
        }
      }
    ServerEssentials.LOG.info("Transfer of Donators Complete.");
  }

  private static void transferMarket(FileDataLoader oldData) {
    ServerEssentials.LOG.info("Starting Transfer of Market Entries");
    if (oldData.getFromKey(DataLoader.DataType.MARKET, new MarketEntry()) != null)
      for (MarketEntry entry :
          oldData.getFromKey(DataLoader.DataType.MARKET, new MarketEntry()).values()) {
        if (SECore.dataLoader.register(DataLoader.DataType.MARKET, entry.seller_uuid, entry)) {
          ServerEssentials.LOG.info("Transferred '{}' MarketEntry", entry.seller_uuid);
          transfers++;
          if (oldData.delete(DataLoader.DataType.MARKET, entry.seller_uuid))
            ServerEssentials.LOG.debug(
                "Deleting MarketEntry '{}' (transferred)", entry.seller_uuid);
          else
            ServerEssentials.LOG.debug(
                "Failed to delete MarketEntry '{}' (transfer)", entry.seller_uuid);
        } else {
          ServerEssentials.LOG.warn(
              "MarketEntry '{}' already exists. (To Prevent issues, merging will not be attempted)",
              entry.seller_uuid);
        }
      }
    ServerEssentials.LOG.info("Transfer of Market Entries Complete.");
  }

  private static void transferRank(FileDataLoader oldData) {
    ServerEssentials.LOG.info("Starting Transfer of Ranks");
    if (oldData.getFromKey(DataLoader.DataType.RANK, new Rank()) != null)
      for (Rank rank : oldData.getFromKey(DataLoader.DataType.RANK, new Rank()).values()) {
        if (SECore.dataLoader.register(DataLoader.DataType.RANK, rank.name, rank)) {
          ServerEssentials.LOG.info("Transferred '{}' Rank", rank.name);
          transfers++;
          if (oldData.delete(DataLoader.DataType.RANK, rank.name))
            ServerEssentials.LOG.debug("Deleting Rank '{}' (transferred)", rank.name);
          else ServerEssentials.LOG.debug("Failed to delete Rank '{}' (transfer)", rank.name);
        } else {
          ServerEssentials.LOG.warn(
              "Rank '{}' already exists. (To Prevent issues, merging will not be attempted)",
              rank.name);
        }
      }
    ServerEssentials.LOG.info("Transfer of Ranks Complete.");
  }

  private static void transferAccount(FileDataLoader oldData) {
    ServerEssentials.LOG.info("Starting Transfer of Accounts");
    if (oldData.getFromKey(DataLoader.DataType.ACCOUNT, new Account()) != null)
      for (Account account :
          oldData.getFromKey(DataLoader.DataType.ACCOUNT, new Account()).values()) {
        if (SECore.dataLoader.register(DataLoader.DataType.ACCOUNT, account.uuid, account)) {
          ServerEssentials.LOG.info("Transferred '{}' Account", account.uuid);
          transfers++;
          if (oldData.delete(DataLoader.DataType.ACCOUNT, account.uuid))
            ServerEssentials.LOG.debug("Deleting Account '{}' (transferred)", account.uuid);
          else ServerEssentials.LOG.debug("Failed to delete Account '{}' (transfer)", account.uuid);
        } else {
          ServerEssentials.LOG.warn(
              "Account '{}' already exists. (To Prevent issues, merging will not be attempted)",
              account.uuid);
        }
      }
    ServerEssentials.LOG.info("Transfer of Accounts Complete.");
  }
}
