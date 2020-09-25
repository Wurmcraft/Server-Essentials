package com.wurmcraft.serveressentials.forge.server.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.basic.Channel;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.NetworkTime;
import com.wurmcraft.serveressentials.forge.api.json.player.NetworkTime.ServerTime;
import com.wurmcraft.serveressentials.forge.api.json.player.ServerPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.chatbridge.event.ChatSocketEvents;
import com.wurmcraft.serveressentials.forge.modules.chatbridge.json.BridgeMessage;
import com.wurmcraft.serveressentials.forge.modules.language.LanguageModule;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.Language;
import com.wurmcraft.serveressentials.forge.server.data.RestDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.events.PlayerDataEvents;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import java.time.Instant;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlayerUtils {

  public static StoredPlayer get(EntityPlayer player) {
    return get(player.getGameProfile().getId());
  }

  public static StoredPlayer get(UUID uuid) {
    return get(uuid.toString());
  }

  public static StoredPlayer get(String uuid) {
    try {
      return (StoredPlayer) SECore.dataHandler.getData(DataKey.PLAYER, uuid);
    } catch (NoSuchElementException e) {
      return newPlayer(uuid, false);
    }
  }

  public static StoredPlayer newPlayer(String uuid, boolean print) {
    GlobalPlayer global = new GlobalPlayer(uuid);
    ServerPlayer server = new ServerPlayer();
    global.firstJoin = Instant.now().getEpochSecond();
    server.firstJoin = Instant.now().getEpochSecond();
    global.lastSeen = Instant.now().getEpochSecond();
    server.lastSeen = Instant.now().getEpochSecond();
    boolean newToNetwork = true;
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      GlobalPlayer restData = RestRequestHandler.User.getPlayer(uuid);
      if (restData != null) {
        global = restData;
        newToNetwork = false;
      }
    }
    StoredPlayer playerData = new StoredPlayer(uuid, server, global);
    NewPlayerEvent event = new NewPlayerEvent(playerData);
    MinecraftForge.EVENT_BUS.post(event);
    playerData = event.newData;
    SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      int code = RestRequestHandler.User.addPlayer(playerData.global);
      if (code != 201) {
        ServerEssentialsServer.LOGGER
            .warn(playerData.uuid + " has failed to save! (" + code + ")");
      }
    }
    boolean finalNewToNetwork = newToNetwork;
    ServerEssentialsServer.EXECUTORS.schedule(() -> {
      String name = UsernameCache.getLastKnownUsername(UUID.fromString(uuid));
      if (print) {
        ServerEssentialsServer.LOGGER.info(
            name + " is new to the server! " + (finalNewToNetwork ? " (New To Network)"
                : " (Has Played on Another Server)"));
        if (finalNewToNetwork) {
          for (EntityPlayer player : FMLCommonHandler.instance()
              .getMinecraftServerInstance()
              .getPlayerList().getPlayers()) {
            ChatHelper.sendHoverMessage(player,
                getLanguage(player).ANNOUNCEMENT_NEW_PLAYER
                    .replaceAll("%PLAYER%", name),
                getLanguage(player).HOVER_PLAYER_NAME.replaceAll("%PLAYER%", name)
                    .replaceAll("%UUID%", uuid));
          }
        }
        if (ModuleLoader.getLoadedModule("ChatBridge") != null
            && ModuleLoader.getLoadedModule("Language") != null) {
          ServerEssentialsServer.EXECUTORS.schedule(() -> {
            Channel ch = (Channel) SECore.dataHandler
                .getData(DataKey.CHANNEL, LanguageModule.config.defaultChannel);
            ChatSocketEvents.socket.sendText(ServerEssentialsServer.GSON.toJson(
                new BridgeMessage(getLanguage(null).ANNOUNCEMENT_NEW_PLAYER
                    .replaceAll("%PLAYER%", name), SECore.config.serverID,
                    uuid, "", ch.getID(), ch.discordChannelID,finalNewToNetwork ? 2 : 3)));
          }, 0, TimeUnit.SECONDS);
        }
      } else {
        ServerEssentialsServer.LOGGER
            .info(name + " was errored, but has now been corrected");
      }
    }, 1, TimeUnit.SECONDS);
    return playerData;
  }

  public static Language getLanguage(ICommandSender sender) {
    if (sender instanceof EntityPlayer) {
      StoredPlayer playerData = get((EntityPlayer) sender);
      try {
        return (Language) SECore.dataHandler
            .getData(DataKey.LANGUAGE, playerData.global.language);
      } catch (NoSuchElementException ignored) {
      }
    }
    return (Language) SECore.dataHandler
        .getData(DataKey.LANGUAGE, SECore.config.defaultLang);
  }

  public static StoredPlayer syncPlayTime(UUID uuid) {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      GlobalPlayer globalData = RestRequestHandler.User.getPlayer(uuid.toString());
      StoredPlayer playerData = PlayerUtils.get(uuid.toString());
      playerData.global = globalData;
      return addPlaytime(playerData, (int) PlayerDataEvents.calculatePlaytime(uuid));
    } else {
      return addPlaytime(get(uuid), (int) PlayerDataEvents.calculatePlaytime(uuid));
    }
  }

  public static StoredPlayer syncPlayTime(UUID uuid, GlobalPlayer globalData) {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      StoredPlayer playerData = PlayerUtils.get(uuid.toString());
      playerData.global = globalData;
      return addPlaytime(playerData, (int) PlayerDataEvents.calculatePlaytime(uuid));
    } else {
      return addPlaytime(get(uuid), (int) PlayerDataEvents.calculatePlaytime(uuid));
    }
  }

  private static StoredPlayer addPlaytime(StoredPlayer playerData, int amount) {
    boolean added = false;
    if (playerData.global != null) {
      for (ServerTime time : playerData.global.playtime.serverTime) {
        if (time.serverID.equalsIgnoreCase(SECore.config.serverID)) {
          time.time = time.time + amount;
          added = true;
        }
      }
      if (!added) {
        List<ServerTime> time = new ArrayList<>();
        Collections.addAll(time, playerData.global.playtime.serverTime);
        time.add(new ServerTime(SECore.config.serverID, amount));
        playerData.global.playtime = new NetworkTime(time.toArray(new ServerTime[0]));
      }
    }
    return playerData;
  }

  public static int getTotalPlayTime(EntityPlayer player) {
    StoredPlayer data = get(player);
    int time = 0;
    for (ServerTime t : data.global.playtime.serverTime) {
      time = (int) (time + t.time);
    }
    return time;
  }

  public static long getCommandCooldown(EntityPlayer player, String command) {
    StoredPlayer playerData = get(player);
    if (playerData.server.commandUsage != null) {
      return playerData.server.commandUsage
          .getOrDefault(command, System.currentTimeMillis());
    } else {
      playerData.server.commandUsage = new HashMap<>();
    }
    return System.currentTimeMillis();
  }

  public static void setCooldown(EntityPlayer player, String command, int amount) {
    StoredPlayer playerData = get(player);
    playerData.server.commandUsage
        .put(command, System.currentTimeMillis() + (amount * 1000));
    SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
  }

  public static UUID getPlayer(String playerName) {
    for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      if (p.getDisplayNameString().equalsIgnoreCase(playerName)) {
        return p.getGameProfile().getId();
      }
    }
    for (UUID id : UsernameCache.getMap().keySet()) {
      if (UsernameCache.getMap().get(id).equalsIgnoreCase(playerName)) {
        return id;
      }
    }
    return null;
  }
}
