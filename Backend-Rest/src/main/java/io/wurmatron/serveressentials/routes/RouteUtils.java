package io.wurmatron.serveressentials.routes;

public class RouteUtils {

    /**
     * Creates a formatted json output with the provided info
     *
     * @param title   title of the message
     * @param message information about the given state
     * @return json message with the included information
     */
    public static String response(String title, String message) {
        return "{" +
                "\"title\": " + "\"" + title + "\"" +
                "\"message\": " + "\"" + message + "\"" +
                "}";
    }
}
