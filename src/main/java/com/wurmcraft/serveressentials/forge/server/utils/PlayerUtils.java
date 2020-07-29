package com.wurmcraft.serveressentials.forge.server.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.ServerPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.Language;
import java.util.NoSuchElementException;
import java.util.UUID;
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
    StoredPlayer playerData = new StoredPlayer(uuid, server, global);
    NewPlayerEvent event = new NewPlayerEvent(playerData);
    MinecraftForge.EVENT_BUS.post(event);
    playerData = event.newData;
    SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
    String playerName = UsernameCache.getLastKnownUsername(UUID.fromString(uuid));
    if (print) {
      ServerEssentialsServer.LOGGER.info(playerName + " is new to the server!");
      for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
          .getPlayerList().getPlayers()) {
        ChatHelper.sendHoverMessage(player,
            getLanguage(player).ANNOUNCEMENT_NEW_PLAYER
                .replaceAll("%PLAYER%", playerName),
            getLanguage(player).HOVER_PLAYER_NAME.replaceAll("%PLAYER%", playerName)
                .replaceAll("%UUID%", uuid));
      }
    } else {
      ServerEssentialsServer.LOGGER.info(playerName + " was errored, but has now been corrected");
    }
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
}
