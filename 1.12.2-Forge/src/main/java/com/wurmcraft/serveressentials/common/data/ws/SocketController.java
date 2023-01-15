package com.wurmcraft.serveressentials.common.data.ws;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.WSWrapper;
import com.wurmcraft.serveressentials.api.models.data_wrapper.ChatMessage;
import com.wurmcraft.serveressentials.api.models.data_wrapper.ConfirmMessage;
import com.wurmcraft.serveressentials.api.models.data_wrapper.ShutdownMessage;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import java.io.IOException;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SocketController {

  public static boolean connected = false;

  private WebSocket ws;

  public SocketController() {
    if (!connected) {
      connected = true;
      WebSocketFactory factory = new WebSocketFactory();
      try {
        // Get Auth token
        if (RequestGenerator.token == null || RequestGenerator.token.isEmpty()) {
          if (((RestDataLoader) SECore.dataLoader).login()) {
            ServerEssentials.LOG.info(
                "Logged into Rest API as '" + ServerEssentials.config.general.serverID
                    + "'");
          } else {
            ServerEssentials.LOG.fatal("Failed to login to Rest API");
          }
        }
        // Setup Web socket
        ws = factory.createSocket(createURL())
            .addHeader("cookie", "authentication=" + RequestGenerator.token);
        ws.addListener(new WebSocketAdapter() {
          @Override
          public void onTextMessage(WebSocket websocket, String text) throws Exception {
            handleTextMessage(ServerEssentials.GSON.fromJson(text, WSWrapper.class));
          }
        });
        ws.connect();
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (WebSocketException e) {
        ServerEssentials.LOG.error(
            "Failed to connect to rest api (" + e.getMessage() + ")");
        throw new RuntimeException(e);
      }
    }
  }

  private static String createURL() {
    boolean https = RequestGenerator.BASE_URL.startsWith("https://");
    return (https ? "wss://" : "ws://")
        + RequestGenerator.BASE_URL.replaceAll((https ? "https://" : "http://"), "")
        + "api/live";
  }

  public void handleTextMessage(WSWrapper wrapper) {
    ServerEssentials.LOG.debug("WS Message: " + wrapper.type + " " + wrapper.data);
    if (wrapper.data.type.equals("broadcast")) {
      ChatMessage broadcast = ServerEssentials.GSON.fromJson(wrapper.data.data,
          ChatMessage.class);
      ChatHelper.sendToAll(
          TextFormatting.RED + "[" + broadcast.senderName + "] " + TextFormatting.GOLD
              + broadcast.message);
    }
    if (wrapper.data.type.equals("chat")) {
      ChatMessage msg = ServerEssentials.GSON.fromJson(wrapper.data.data,
          ChatMessage.class);
      Channel ch = SECore.dataLoader.get(DataType.CHANNEL, msg.channel, new Channel());
      if (ch != null) {
        HashMap<EntityPlayer, LocalAccount> players = ChatHelper.getInChannel(ch);
        for (EntityPlayer player : players.keySet()) {
          if (!ChatHelper.isIgnored(players.get(player), msg.senderName)) {
            ChatHelper.send(player, msg.senderName + " " + msg.message); // TODO Format?
          }
        }
      } else {
        // TODO Handle Channel mismatch
      }
    }
    if (wrapper.data.type.equalsIgnoreCase("Confirmation")) {
      ConfirmMessage msg = ServerEssentials.GSON.fromJson(wrapper.data.data,
          ConfirmMessage.class);
      sendConfirmation(msg);
    }
    // TODO Handle Discord
    if (wrapper.data.type.equalsIgnoreCase("shutdown")) {
      ShutdownMessage shutdown = ServerEssentials.GSON.fromJson(wrapper.data.data,
          ShutdownMessage.class);
      shutdown(shutdown);
    }
  }

  private static void sendConfirmation(ConfirmMessage msg) {
    for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      if (msg.sender.equalsIgnoreCase(player.getGameProfile().getId().toString())) {
        ChatHelper.send(player, SECore.dataLoader.get(DataType.LANGUAGE,
            SECore.dataLoader.get(DataType.ACCOUNT,
                player.getGameProfile().getId().toString(), new Account()).lang,
            new Language()).DM_CONFIRM.replaceAll("\\{@PLAYER@}",
            PlayerUtils.getUsernameForInput(msg.receiver) != null
                ? PlayerUtils.getUsernameForInput(msg.receiver) : msg.receiver));
      }
    }
  }

  private static void shutdown(ShutdownMessage message) {
    if (message.id.equals("API")) {
      ChatHelper.sendToAll(message.message);
      ServerEssentials.LOG.info("Shutdown message received from API: " + message.message);
      FMLCommonHandler.instance().getMinecraftServerInstance().saveAllWorlds(false);
      FMLCommonHandler.instance().getMinecraftServerInstance().initiateShutdown();
    } else {
      // TODO Handle Shutdown message from non-api other servers?
    }
  }

  public void send(WSWrapper wrapper) throws IOException, WebSocketException {
    ws.sendText(ServerEssentials.GSON.toJson(wrapper));
  }
}
