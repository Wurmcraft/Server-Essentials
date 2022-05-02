/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.rest;

import io.javalin.Javalin;
import io.javalin.http.util.RedirectToLowercasePathPlugin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.model.config.BackendConfig;

public class JavalinConfigurator {

  public static Javalin setupAndConfigure(BackendConfig config, String[] args) {
    Javalin javalin =
        Javalin.create(
            (cfg) -> {
              // Config
              cfg.defaultContentType = "application/json";
              cfg.autogenerateEtags = true;
              cfg.showJavalinBanner = false;
              if (!config.Server.corosOrigins.isEmpty()) {
                cfg.enableCorsForOrigin(config.Server.corosOrigins.split(","));
              }
              cfg.asyncRequestTimeout = config.Server.requestTimeout;
              //      cfg.enforceSsl = !config.general.testing;
              // Plugins
              if (config.Server.forceLowercase) {
                cfg.registerPlugin(new RedirectToLowercasePathPlugin());
              }
              if (config.Server.swaggerEnabled) {
                cfg.registerPlugin(
                    new OpenApiPlugin(
                        new OpenApiOptions(
                                new Info()
                                    .version(ServerEssentialsBackend.VERSION)
                                    .description("Server Essentials Rest API"))
                            .path("api/swagger")
                            .swagger(
                                new SwaggerOptions("/swagger").title("Server-Essentials Swagger"))
                            .roles(Route.RestRoles.values())));
                ServerEssentialsBackend.LOG.info(
                    "Connect to swagger http://"
                        + config.Server.host
                        + ":"
                        + config.Server.port
                        + "/swagger");
              }
              cfg.requestLogger(
                  (ctx, ms) -> {
                    ServerEssentialsBackend.LOG.debug(
                        ctx.ip()
                            + " "
                            + ctx.method()
                            + " "
                            + ctx.path()
                            + " ("
                            + Math.round(ms)
                            + "ms)");
                  });
              cfg.accessManager(
                  (handler, ctx1, routeRoles) ->
                      handler.handle(
                          ctx1)); // TODO Endpoint access handler / security / auth management
            });
    RouteLoader.registerRoutes(javalin);
    javalin.start(config.Server.host, config.Server.port);
    return javalin;
  }
}
