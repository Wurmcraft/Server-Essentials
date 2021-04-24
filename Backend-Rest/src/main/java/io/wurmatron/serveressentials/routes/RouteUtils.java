package io.wurmatron.serveressentials.routes;

import io.wurmatron.serveressentials.models.MessageResponse;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;

public class RouteUtils {

    /**
     * Creates a formatted json output with the provided info
     *
     * @param title   title of the message
     * @param message information about the given state
     * @return json message with the included information
     */
    public static String response(String title, String message) {
        return GSON.toJson(new MessageResponse(title, message));
    }
}
