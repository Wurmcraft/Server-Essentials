package com.wurmcraft.serveressentials.forge.modules.chatbridge.event;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
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
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class ChatSocketEvents {

  public static WebSocket socket;

  public static void setup() {
    try {
      socket = connect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SubscribeEvent
  public void onChat(ServerChatEvent e) {
    Channel ch = null;
    try {
      ch = (Channel) SECore.dataHandler
          .getData(DataKey.CHANNEL, PlayerUtils.get(e.getPlayer()).server.channel);
    } catch (NoSuchElementException f) {
      ch = (Channel) SECore.dataHandler
          .getData(DataKey.CHANNEL, LanguageModule.config.defaultChannel);
    }
    socket.sendText(ServerEssentialsServer.GSON.toJson(
        new BridgeMessage(e.getMessage(), SECore.config.serverID,
            e.getPlayer().getGameProfile().getId().toString(),
            Objects.requireNonNull(RankUtils.getRank(e.getPlayer())).prefix + " " + e
                .getUsername(), ch.getID(), ch.discordChannelID,
            LanguageModule.config.defaultChannel.equals(ch.getID()) ? 1 : 0)));
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerLeave(PlayerLoggedOutEvent e) {
    try {
      Channel ch = (Channel) SECore.dataHandler
          .getData(DataKey.CHANNEL, DiscordModule.config.playerLoginNotificationChannel);
      socket.sendText(ServerEssentialsServer.GSON
          .toJson(new BridgeMessage("[-] " + e.player.getDisplayNameString(),
              SECore.config.serverID, e.player.getGameProfile().getId().toString(),
              e.player.getDisplayNameString(), ch.getID(), ch.discordChannelID, 3)));
    } catch (NoSuchElementException ignored) {
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerLeave(PlayerLoggedInEvent e) {
    try {
      Channel ch = (Channel) SECore.dataHandler
          .getData(DataKey.CHANNEL, DiscordModule.config.playerLoginNotificationChannel);
      socket.sendText(ServerEssentialsServer.GSON
          .toJson((new BridgeMessage("[+] " + e.player.getDisplayNameString(),
              SECore.config.serverID, e.player.getGameProfile().getId().toString(),
              e.player.getDisplayNameString(), ch.getID(), ch.discordChannelID, 3))));
    } catch (NoSuchElementException f) {
      f.printStackTrace();
    }
  }

  private static BridgeMessage lastMessage = null;

  private static WebSocket connect() throws IOException, WebSocketException {
    return new WebSocketFactory()
        .setConnectionTimeout(5000)
        .createSocket(RestRequestHandler.BASE_URL + "chat")
        .addHeader("token", SECore.config.Rest.restAuth)
        .addHeader("serverID", SECore.config.serverID)
        .addListener(new WebSocketAdapter() {
          @Override
          public void onTextMessage(WebSocket websocket, String message) {
            BridgeMessage msg = ServerEssentialsServer.GSON
                .fromJson(message, BridgeMessage.class);
            if (!msg.id.equals(SECore.config.serverID) && !msg.equals(lastMessage)) {
              lastMessage = msg;
              handleMessage(msg);
            }
          }
        })
        .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
        .connect();
  }

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
}
