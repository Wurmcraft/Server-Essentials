package com.wurmcraft.serveressentials.forge.modules.chatbridge.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Channel;
import com.wurmcraft.serveressentials.forge.modules.chatbridge.json.BridgeMessage;
import com.wurmcraft.serveressentials.forge.modules.discord.DiscordModule;
import com.wurmcraft.serveressentials.forge.modules.language.LanguageModule;
import com.wurmcraft.serveressentials.forge.modules.language.utils.LanguageUtils;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ChatPollEvents {


  public static void handleMessage(BridgeMessage msg) {
    try {
      Channel ch = (Channel) SECore.dataHandler.getData(DataKey.CHANNEL, msg.channel);
      ITextComponent formattedMessage = new TextComponentString(
          LanguageModule.config.chatFormat
              .replaceAll("%NAME%", msg.displayName.replaceAll("&", "\u00a7"))
              .replaceAll("%MESSAGE%", msg.message)
              .replaceAll("%CHANNEL%", ch.prefix.replaceAll("&", "\u00a7"))
              .replaceAll("%PREFIX%", TextFormatting.GOLD +
                  "[" + msg.id.substring(0, 1).toUpperCase() + msg.id.substring(1) + "]")
              .replaceAll("%SUFFIX%", ""));
      if (msg.discordChannelID
          .equals(
              ch.discordChannelID)) { // Invalid means its for another server's local or another server sharing the same channel
        LanguageUtils.sendMessageToChannel(ch, formattedMessage);
      }
    } catch (NoSuchElementException e) {

    }
  }

  public static void sendMessage(BridgeMessage msg) {
    ServerEssentialsServer.EXECUTORS.schedule(() -> {
      RestRequestHandler.Bridge.addMessage(msg);
    }, 0, TimeUnit.SECONDS);
  }
}
