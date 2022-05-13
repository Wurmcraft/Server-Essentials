package com.wurmcraft.serveressentials.common.data.ws;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.wurmcraft.serveressentials.api.models.WSWrapper;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import java.io.IOException;

public class SocketController {

  private static WebSocket ws;

  public static synchronized void connect() throws IOException, WebSocketException {
    if (ws == null) {
      String connectionURL = createURL();
      ws =
          new WebSocketFactory()
              .createSocket(connectionURL)
              .addHeader("cookie", "authentication=" + RequestGenerator.token);
      ws.addListener(
          new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
              try {
                WSWrapper wrapper = GSON.fromJson(text, WSWrapper.class);
                handle(wrapper);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });
      ws.connect();
      ws.setPingInterval(60 * 1000); // TODO Replace with Status update
    } else ws.recreate().connect();
  }

  private static String createURL() {
    boolean https = RequestGenerator.BASE_URL.startsWith("https://");
    return (https ? "wss://" : "ws://")
        + RequestGenerator.BASE_URL.replaceAll((https ? "https://" : "http://"), "")
        + "api/live";
  }

  public static synchronized void send(WSWrapper wrapper) throws IOException, WebSocketException {
    if (ws == null || !ws.isOpen()) connect();
    ws.sendText(GSON.toJson(wrapper));
  }

  public static void handle(WSWrapper wrapper) {}
}
