package io.wurmatron.serveressentials.routes.ws;

import io.javalin.websocket.WsContext;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class LiveConnectionController {

    public NonBlockingHashMap<WsContext, String> wsToken = new NonBlockingHashMap<>();

}
