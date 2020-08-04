package com.wurmcraft.serveressentials.forge.server.data;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank;
import com.wurmcraft.serveressentials.forge.api.json.basic.CurrencyConversion;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.ServerPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.api.json.rest.GlobalBan;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerChunkData;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerChunkData.PlayerChunkData;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler.ChunkLoading;
import java.util.NoSuchElementException;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class RestDataHandler extends FileDataHandler {

  @Override
  public <T extends JsonParser> NonBlockingHashMap<String, T> getDataFromKey(DataKey key,
      T type) {
    try {
      return super.getDataFromKey(key, type);
    } catch (NoSuchElementException e) {
      try {
        if (key == DataKey.AUTO_RANK) {
          NonBlockingHashMap<String, T> restData = new NonBlockingHashMap<>();
          for (AutoRank ar : RestRequestHandler.AutoRank.getAutoRanks()) {
            restData.put(ar.getID(), (T) ar);
            registerData(DataKey.AUTO_RANK, ar);
          }
          return restData;
        } else if (key == DataKey.CURRENCY) {
          NonBlockingHashMap<String, T> restData = new NonBlockingHashMap<>();
          for (CurrencyConversion convert : RestRequestHandler.Economy
              .getAllCurrencies()) {
            restData.put(convert.getID(), (T) convert);
            registerData(DataKey.CURRENCY, convert);
          }
          return restData;
        } else if (key == DataKey.RANK) {
          NonBlockingHashMap<String, T> restData = new NonBlockingHashMap<>();
          for (Rank rank : RestRequestHandler.Rank.getAllRanks()) {
            restData.put(rank.getID(), (T) rank);
            registerData(DataKey.RANK, rank);
          }
          return restData;
        } else if (key == DataKey.BAN) {
          NonBlockingHashMap<String, T> restData = new NonBlockingHashMap<>();
          for (GlobalBan ban : RestRequestHandler.Ban.getGlobalBans()) {
            restData.put(ban.getID(), (T) ban);
            registerData(DataKey.RANK, ban);
          }
          return restData;
        } else if (key == DataKey.CHUNK_LOADING) {
          NonBlockingHashMap<String, T> restData = new NonBlockingHashMap<>();
          for (PlayerChunkData data : ChunkLoading.getLoadedChunks().playerChunks) {
            restData.put(data.getID(), (T) data);
            registerData(DataKey.CHUNK_LOADING, data);
          }
          return restData;
        }
      } catch (Exception f) {
        if (SECore.config.debug) {
          ServerEssentialsServer.LOGGER.info(
              "Failed to cast '" + key.getName() + "' to " + type.getClass().getName()
                  + "(" + type.getID() + ")");
        }
        return new NonBlockingHashMap<>();
      }
    }
    return new NonBlockingHashMap<>();
  }

  @Override
  public JsonParser getData(DataKey key, String dataID) throws NoSuchElementException {
    try {
      return super.getData(key, dataID);
    } catch (NoSuchElementException e) {
      if (key == DataKey.AUTO_RANK) {
        AutoRank ar = RestRequestHandler.AutoRank.getAutoRank(dataID);
        if (ar != null) {
          registerData(DataKey.AUTO_RANK, ar);
          return ar;
        } else if (SECore.config.debug) {
          ServerEssentialsServer.LOGGER.error(
              "Failed to find '" + key.getName() + ":" + dataID + "' on "
                  + SECore.config.Rest.restURL);
        }
      } else if (key == DataKey.CURRENCY) {
        CurrencyConversion convert = RestRequestHandler.Economy.getCurrency(dataID);
        if (convert != null) {
          registerData(DataKey.CURRENCY, convert);
          return convert;
        } else if (SECore.config.debug) {
          ServerEssentialsServer.LOGGER.error(
              "Failed to find '" + key.getName() + ":" + dataID + "' on "
                  + SECore.config.Rest.restURL);
        }
      } else if (key == DataKey.PLAYER) {
        GlobalPlayer player = RestRequestHandler.User.getPlayer(dataID);
        if (player != null) {
          try {
            StoredPlayer playerData = new StoredPlayer(dataID,new ServerPlayer(), player);
            registerData(DataKey.PLAYER, playerData);
          } catch (NoSuchElementException f) {
            throw new NoSuchElementException();
          }
        } else if (SECore.config.debug) {
          ServerEssentialsServer.LOGGER.error(
              "Failed to find '" + key.getName() + ":" + dataID + "' on "
                  + SECore.config.Rest.restURL);
        }
      } else if (key == DataKey.RANK) {
        Rank rank = RestRequestHandler.Rank.getRank(dataID);
        if (rank != null) {
          registerData(DataKey.RANK, rank);
          return rank;
        } else if (SECore.config.debug) {
          ServerEssentialsServer.LOGGER.error(
              "Failed to find '" + key.getName() + ":" + dataID + "' on "
                  + SECore.config.Rest.restURL);
        }
      }
    }
    throw new NoSuchElementException();
  }

  @Override
  public void registerData(DataKey key, JsonParser dataToStore) {
    super.registerData(key, dataToStore);
    if (key == DataKey.AUTO_RANK) {
      if (dataToStore instanceof AutoRank) {
        RestRequestHandler.AutoRank.addAutoRank((AutoRank) dataToStore);
      } else {
        ServerEssentialsServer.LOGGER.error(
            "Failed to update database with invalid " + key.getName() + " with type '"
                + dataToStore.getClass() + "'");
      }
    } else if (key == DataKey.BAN) {
      if (dataToStore instanceof GlobalBan) {
        RestRequestHandler.Ban.addGlobalBans((GlobalBan) dataToStore);
      } else {
        ServerEssentialsServer.LOGGER.error(
            "Failed to update database with invalid " + key.getName() + " with type '"
                + dataToStore.getClass() + "'");
      }
    } else if (key == DataKey.CHUNK_LOADING) {
      if (dataToStore instanceof PlayerChunkData) {
        NonBlockingHashMap<String, PlayerChunkData> playerData = getDataFromKey(
            DataKey.CHUNK_LOADING, new PlayerChunkData());
        if (dataToStore instanceof PlayerChunkData) {
          playerData.put(dataToStore.getID(), (PlayerChunkData) dataToStore);
          ServerChunkData serverData = new ServerChunkData(SECore.config.serverID,
              playerData.values().toArray(new PlayerChunkData[0]));
          RestRequestHandler.ChunkLoading.overrideLoadedChunks(serverData);
        } else {
          ServerEssentialsServer.LOGGER.error(
              "Failed to update database with invalid " + key.getName() + " with type '"
                  + dataToStore.getClass() + "'");
        }
      } else {
        ServerEssentialsServer.LOGGER.error(
            "Failed to update database with invalid " + key.getName() + " with type '"
                + dataToStore.getClass() + "'");
      }
    } else if (key == DataKey.CURRENCY) {
      if (dataToStore instanceof CurrencyConversion) {
        RestRequestHandler.Economy.addCurrency((CurrencyConversion) dataToStore);
      } else {
        ServerEssentialsServer.LOGGER.error(
            "Failed to update database with invalid " + key.getName() + " with type '"
                + dataToStore.getClass() + "'");
      }
    } else if (key == DataKey.PLAYER) {
      // Do nothing due to data-sync errors, requires manually updating values on database based on certain conditions
    } else if (key == DataKey.RANK) {
      if (dataToStore instanceof Rank) {
        RestRequestHandler.Rank.addRank((Rank) dataToStore);
      } else {
        ServerEssentialsServer.LOGGER.error(
            "Failed to update database with invalid " + key.getName() + " with type '"
                + dataToStore.getClass() + "'");
      }
    }
  }

  @Override
  public void delData(DataKey key, String dataToRemove, boolean deleteFromDisk)
      throws NoSuchElementException {
    if (deleteFromDisk && !ServerEssentialsServer.isUpdateInProgress) {
      if (key == DataKey.AUTO_RANK) {
        try {
          AutoRank ar = (AutoRank) getData(DataKey.AUTO_RANK, dataToRemove);
          RestRequestHandler.AutoRank.deleteAutoRank(ar);
        } catch (NoSuchElementException e) {
          if (SECore.config.debug) {
            ServerEssentialsServer.LOGGER
                .error(dataToRemove + " was already removed from " + key.getName());
          }
        }
      } else if (key == DataKey.BAN) {
        try {
          GlobalBan ban = (GlobalBan) getData(DataKey.BAN, dataToRemove);
          RestRequestHandler.Ban.deleteBan(ban);
        } catch (NoSuchElementException e) {
          if (SECore.config.debug) {
            ServerEssentialsServer.LOGGER
                .error(dataToRemove + " was already removed from " + key.getName());
          }
        }
      } else if (key == DataKey.CURRENCY) {
        try {
          CurrencyConversion currency = (CurrencyConversion) getData(DataKey.CURRENCY,
              dataToRemove);
          RestRequestHandler.Economy.delCurrency(currency);
        } catch (NoSuchElementException e) {
          if (SECore.config.debug) {
            ServerEssentialsServer.LOGGER
                .error(dataToRemove + " was already removed from " + key.getName());
          }
        }
      } else if (key == DataKey.RANK) {
        try {
          Rank rank = (Rank) getData(DataKey.RANK, dataToRemove);
          RestRequestHandler.Rank.deleteRank(rank);
        } catch (NoSuchElementException e) {
          if (SECore.config.debug) {
            ServerEssentialsServer.LOGGER
                .error(dataToRemove + " was already removed from " + key.getName());
          }
        }
      }
    }
    super.delData(key, dataToRemove, deleteFromDisk);
  }
}
