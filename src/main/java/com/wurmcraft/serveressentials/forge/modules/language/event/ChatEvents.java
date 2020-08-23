package com.wurmcraft.serveressentials.forge.modules.language.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.basic.Channel;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.language.LanguageModule;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.Global;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatEvents {

  protected static Map<UUID, String[]> lastChat = new HashMap<>();
  public static final Logger LOGGER = LogManager.getLogger("Chat");

  @SubscribeEvent
  public void newPlayer(NewPlayerEvent e) {
    e.newData.global.language = SECore.config.defaultLang;
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onChat(ServerChatEvent e) {
    if (PlayerUtils.get(e.getPlayer()).global.muted) {
      if (PlayerUtils.get(e.getPlayer()).global.muteExpire > System.currentTimeMillis()) {
        ChatHelper.sendMessage(e.getPlayer(),
            PlayerUtils.getLanguage(e.getPlayer()).LANGUAGE_MUTED);
        e.setCanceled(true);
        return;
      } else {
        StoredPlayer playerData = PlayerUtils.get(e.getPlayer());
        playerData.global.muted = false;
        playerData.global.muteExpire = 0;
        if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
          RestRequestHandler.User.overridePlayer(playerData.uuid, playerData.global);
        }
        SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
      }
    }
    StoredPlayer playerData = PlayerUtils.get(e.getPlayer());
    if (playerData.server.channel.isEmpty()) {
      playerData.server.channel = LanguageModule.config.defaultChannel;
      SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
    }
    if (canHandleMessage(e.getPlayer().getGameProfile().getId(), e.getMessage())) {
      Rank rank = (Rank) SECore.dataHandler
          .getData(DataKey.RANK, PlayerUtils.get(e.getPlayer()).global.rank);
      ITextComponent msg = formatMessage(e.getPlayer(),
          handleUsername(e.getPlayer(), PlayerUtils.get(e.getPlayer())), rank,
          e.getMessage());
      LOGGER.info(msg.getUnformattedText());
      for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
          .getPlayerList().getPlayers()) {
        StoredPlayer data = PlayerUtils.get(player);
        if (data.server.channel.equals(playerData.server.channel)) {
          ChatHelper.sendMessage(player, msg);
        }
      }
      e.setCanceled(true);
    } else {
      ChatHelper.sendMessage(e.getPlayer(),
          PlayerUtils.getLanguage(e.getPlayer()).LANGUAGE_SPAM);
    }
  }

  public static String handleUsername(EntityPlayer player, StoredPlayer playerData) {
    return (playerData.server.nick != null && !playerData.server.nick.isEmpty()) ? "*"
        + playerData.server.nick.replaceAll("&", "\u00a7") : player.getName();
  }

  private static boolean canHandleMessage(UUID name, String message) {
    if (lastChat.containsKey(name)) {
      String[] chat = lastChat.get(name);
      if (chat[0].equalsIgnoreCase(message)) {
        int count = 0;
        for (int index = 0; index < chat.length; index++) {
          if (message.equalsIgnoreCase(chat[index])) {
            count++;
          } else if (chat[index] == null) {
            chat[index] = message;
            count++;
            break;
          }
        }
        return count <= LanguageModule.config.spamLimit;
      } else {
        lastChat.remove(name);
        return true;
      }
    } else {
      String[] chat = new String[LanguageModule.config.spamLimit + 1];
      chat[0] = message;
      lastChat.put(name, chat);
      return true;
    }
  }

  public static ITextComponent formatMessage(EntityPlayer player, String displayName,
      Rank rank, String msg) {
    StoredPlayer playerData = PlayerUtils.get(player);
    Channel channel;
    try {
      channel = (Channel) SECore.dataHandler
          .getData(DataKey.CHANNEL, playerData.server.channel);
    } catch (NoSuchElementException e) {
      channel = (Channel) SECore.dataHandler
          .getData(DataKey.CHANNEL, LanguageModule.config.defaultChannel);
    }
    if (ModuleLoader.getLoadedModule("Rank") != null && RankUtils
        .hasPermission(player, "language.chat.color")
        || ModuleLoader.getLoadedModule("Rank") == null) {
      return new TextComponentString(
          LanguageModule.config.chatFormat
              .replaceAll("%PREFIX%", rank.prefix.replaceAll("&", "\u00a7"))
              .replaceAll("%NAME%", displayName)
              .replaceAll("%SUFFIX%", rank.suffix.replaceAll("&", "\u00a7"))
              .replaceAll("%MESSAGE%", msg.replaceAll("&", "\u00a7"))
              .replaceAll("%DIMENSION%", player.dimension + "")
              .replaceAll("%CHANNEL%", channel.prefix.replaceAll("&", "\u00a7")));
    } else {
      return new TextComponentString(
          LanguageModule.config.chatFormat
              .replaceAll("%PREFIX%", rank.prefix.replaceAll("&", "\u00a7"))
              .replaceAll("%NAME%", displayName)
              .replaceAll("%SUFFIX%", rank.suffix.replaceAll("&", "\u00a7"))
              .replaceAll("%MESSAGE%", msg)
              .replaceAll("%DIMENSION%", player.dimension + "")
              .replaceAll("%CHANNEL%", channel.prefix.replaceAll("&", "\u00a7")));
    }
  }
}
