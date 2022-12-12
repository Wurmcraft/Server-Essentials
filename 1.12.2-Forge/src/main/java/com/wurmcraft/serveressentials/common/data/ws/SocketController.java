package com.wurmcraft.serveressentials.common.data.ws;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.WSWrapper;
import com.wurmcraft.serveressentials.api.models.data_wrapper.ChatMessage;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import java.io.IOException;
import net.minecraft.util.text.TextFormatting;

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
    // TODO Handle Messages
    // Chat
    // Discord
    // Shutdown
  }

  public void send(WSWrapper wrapper) throws IOException, WebSocketException {
    ws.sendText(ServerEssentials.GSON.toJson(wrapper));
  }
}
