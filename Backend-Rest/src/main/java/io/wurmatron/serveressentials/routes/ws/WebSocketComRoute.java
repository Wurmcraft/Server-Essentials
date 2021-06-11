package io.wurmatron.serveressentials.routes.ws;

import io.javalin.websocket.WsHandler;
import io.wurmatron.serveressentials.routes.Route;

import java.util.function.Consumer;

public class WebSocketComRoute {

    @Route(path = "api/live", method = "WS")
    public static Consumer<WsHandler> ws = ws -> {

        ws.onConnect(ctx -> {

        });

        ws.onMessage(ctx -> {

        });

        ws.onClose(ctx -> {

        });

        ws.onError(ctx -> {

        });
    };
}
