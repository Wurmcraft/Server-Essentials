package io.wurmatron.serveressentials.routes.ws;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsHandler;
import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.discord.DiscordBot;
import io.wurmatron.serveressentials.models.AuthUser;
import io.wurmatron.serveressentials.models.DataWrapper;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.WSWrapper;
import io.wurmatron.serveressentials.models.data_wrapper.ChatMessage;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.function.Consumer;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class WebSocketComRoute {

    public static NonBlockingHashMap<WsContext, String> activeConnections = new NonBlockingHashMap<>();

    @Route(path = "api/live", method = "WS")
    public static Consumer<WsHandler> ws = ws -> {

        ws.onConnect(ctx -> {
            String token = ctx.cookie("authentication");
            if (!activeConnections.containsValue(token)) {
                if (EndpointSecurity.authTokens.containsKey(token)) {
                    AuthUser serverPerms = EndpointSecurity.authTokens.get(token);
                    if (serverPerms.type.equalsIgnoreCase("SERVER")) {
                        activeConnections.put(ctx, serverPerms.name);
                        ctx.send(GSON.toJson(new WSWrapper(200, WSWrapper.Type.UPDATE, new DataWrapper(AuthUser.class.getTypeName(), GSON.toJson(serverPerms)))));
                        LOG.info(activeConnections.get(ctx) + " has connected to the Web Socket");
                    } else {
                        ctx.send(GSON.toJson(new WSWrapper(409, WSWrapper.Type.MESSAGE, new DataWrapper(MessageResponse.class.getTypeName(), response("Invalid Type", "Only servers can access the live data stream")))));
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
                ctx.send(GSON.toJson(new WSWrapper(400, WSWrapper.Type.MESSAGE, new DataWrapper(MessageResponse.class.getTypeName(), response("No Auth", "Failed to authenticate")))));
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
            System.out.println("Error: " + ctx.error().getMessage());
        });
    };

    public static void handle(WSWrapper dataWrapper, WsContext ctx) {
        if (dataWrapper.type == WSWrapper.Type.MESSAGE) {
            if (dataWrapper.data.type.equalsIgnoreCase("CHAT")) {
                try {
                    ChatMessage message = GSON.fromJson(dataWrapper.data.data, ChatMessage.class);
                    sendToAllOthers(GSON.toJson(dataWrapper), ctx);
                    // Send on discord bridge
                    if(!ServerEssentialsRest.config.discord.token.isEmpty()) {
                        DiscordBot.sendMessage(message);
                    }
                } catch (Exception e) {
                    LOG.warn("Failed to parse message from '" + activeConnections.get(ctx) + "'");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendToAllOthers(String data, WsContext ctx) {
        for (WsContext context : activeConnections.keySet())
            if (context.equals(ctx))
                context.send(data);
    }
}
