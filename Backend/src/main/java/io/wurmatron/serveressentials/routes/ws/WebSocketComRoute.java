/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.routes.ws;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.discord.DiscordBot;
import io.wurmatron.serveressentials.models.AuthUser;
import io.wurmatron.serveressentials.models.DMMessage;
import io.wurmatron.serveressentials.models.DataWrapper;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.ServerStatus;
import io.wurmatron.serveressentials.models.WSWrapper;
import io.wurmatron.serveressentials.models.data_wrapper.ChatMessage;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.routes.informational.StatusRoutes;
import java.util.Map;
import java.util.function.Consumer;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class WebSocketComRoute {

  public static NonBlockingHashMap<WsContext, String> activeConnections =
      new NonBlockingHashMap<>();

    @Route(path = "api/live", method = "WS")
    public static Consumer<WsConfig> ws = ws -> {
      ws.onConnect(ctx -> {
        Map<String, String> cookies = ctx.cookieMap();
        String token = cookies.get("authentication");
        if (!activeConnections.containsValue(token)) {
          if (EndpointSecurity.authTokens.containsKey(token)) {
            AuthUser serverPerms = EndpointSecurity.authTokens.get(token);
            if (serverPerms.type.equalsIgnoreCase("SERVER")) {
//              if (activeConnections.containsValue(serverPerms.name)) {
//                  for (WsContext wsContext : activeConnections.keySet()) {
//                      if (activeConnections.get(wsContext).equals(serverPerms.name)) {
//                          wsContext.session.close();
//                          activeConnections.remove(wsContext);
//                          LOG.warn(serverPerms.name
//                              + " tried to login twice, closing socket for older connection");
//                      }
//                  }
//              }
              activeConnections.put(ctx, serverPerms.name);
              ctx.send(GSON.toJson(new WSWrapper(200, WSWrapper.Type.UPDATE,
                  new DataWrapper(AuthUser.class.getTypeName(),
                      GSON.toJson(serverPerms)))));
              LOG.info(activeConnections.get(ctx) + " has connected to the Web Socket");
            } else {
              ctx.send(GSON.toJson(new WSWrapper(409, WSWrapper.Type.MESSAGE,
                  new DataWrapper(MessageResponse.class.getTypeName(),
                      response("Invalid Type",
                          "Only servers can access the live data stream")))));
              ctx.session.close();
            }
          }
        }
      });

      ws.onMessage(ctx -> {
        if (activeConnections.containsKey(ctx)) {
          WSWrapper message = GSON.fromJson(ctx.message(), WSWrapper.class);
          handle(message, ctx);
        } else {
          ctx.send(GSON.toJson(new WSWrapper(400, WSWrapper.Type.MESSAGE,
              new DataWrapper(MessageResponse.class.getTypeName(),
                  response("No Auth", "Failed to authenticate")))));
          ctx.session.close();
        }
      });

      ws.onClose(ctx -> {
        if (activeConnections.containsKey(ctx)) {
          LOG.info(activeConnections.get(ctx) + " has disconnected from the Web Socket");
          activeConnections.remove(ctx);
        }
      });

      ws.onError(ctx -> {
        if(ctx.error() != null)
        LOG.error(ctx.error().getLocalizedMessage());
      });
    };

  public static void handle(WSWrapper dataWrapper, WsContext ctx) {
    if (dataWrapper.type == WSWrapper.Type.MESSAGE) {
      if (dataWrapper.data.type.equalsIgnoreCase("CHAT")) {
        try {
          ChatMessage message = GSON.fromJson(dataWrapper.data.data, ChatMessage.class);
          sendToAllOthers(GSON.toJson(dataWrapper), ctx);
          // Send on discord bridge
          if (!ServerEssentialsRest.config.discord.token.isEmpty()) {
            DiscordBot.sendMessage(message);
          }
          LOG.info("[Chat]: (" + message.serverID + ":"  + message.channel + ") " +  message.senderName + " > " + message.message);
        } catch (Exception e) {
          LOG.warn("Failed to parse message from '" + activeConnections.get(ctx) + "'");
          e.printStackTrace();
        }
      } else if (dataWrapper.data.type.equalsIgnoreCase("Status")) {
        try {
          ServerStatus status = GSON.fromJson(dataWrapper.data.data, ServerStatus.class);
          StatusRoutes.lastServerStatus.put(status.serverID, status);
          sendToAllOthers(GSON.toJson(dataWrapper), ctx);
        } catch (Exception e) {
          LOG.warn("Failed to parse message from '" + activeConnections.get(ctx) + "'");
          e.printStackTrace();
        }
      } else if (dataWrapper.data.type.equalsIgnoreCase("DM")) {
        try {
          DMMessage dmMSG = GSON.fromJson(dataWrapper.data.data, DMMessage.class);
          if (sendToOtherPlayerUUID(GSON.toJson(dmMSG), dmMSG.receiverID)) {
            //                        ctx.send() TODO Send Confirmation
          }
        } catch (Exception e) {
          LOG.warn("Failed to parse message from '" + activeConnections.get(ctx) + "'");
          e.printStackTrace();
        }
      }
    }
  }

  public static void sendToAllOthers(String data, WsContext ctx) {
    for (WsContext context : activeConnections.keySet()) {
      if (!context.equals(ctx)) {
        context.send(data);
      }
    }
  }

  public static boolean sendToOtherPlayerUUID(String data, String p) {
    for (WsContext context : activeConnections.keySet()) {
      ServerStatus lastStatus = StatusRoutes.lastServerStatus.get(activeConnections.get(context));
      for (String player : lastStatus.onlinePlayers) {
        if (player.equalsIgnoreCase(p)) {
          context.send(data);
          return true;
        }
      }
    }
    return false;
  }
}
