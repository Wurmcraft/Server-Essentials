/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.routes;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.wurmatron.serveressentials.models.MessageResponse;
import java.lang.reflect.Field;

public class RouteUtils {

  /**
   * Creates a formatted json output with the provided info
   *
   * @param title title of the message
   * @param message information about the given state
   * @return json message with the included information
   */
  public static String response(String title, String message) {
    return GSON.toJson(new MessageResponse(title, message));
  }

  public static void setupExceptions(Javalin app) {
    app.exception(
        BadRequestResponse.class,
        (e, ctx) -> {
          ctx.contentType("application/json")
              .result(response("Bad Request", e.getMessage()));
        });
  }

  /**
   * Removes / sets all the fields to null except the one provided
   *
   * @param instance instance to remove everything from
   * @param safe field to keep in the account instance
   * @return instance with all but one field has been removed / null'd
   * @throws IllegalAccessException This should never happen, unless Account has been modified
   */
  public static <T extends Object> T wipeAllExceptField(T instance, Field safe)
      throws IllegalAccessException {
    for (Field field : instance.getClass().getDeclaredFields()) {
      if (!field.equals(safe)) {
        field.set(instance, null);
      }
    }
    if (safe.get(instance) instanceof String && ((String) safe.get(instance)).isEmpty()) {
      safe.set(instance, "");
    }
    return instance;
  }
}
