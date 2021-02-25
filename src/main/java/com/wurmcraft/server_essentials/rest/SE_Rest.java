package com.wurmcraft.server_essentials.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.server_essentials.rest.config.ConfigLoader;
import com.wurmcraft.server_essentials.rest.config.RestConfig;
import com.wurmcraft.server_essentials.rest.routes.Routes;
import com.wurmcraft.server_essentials.rest.sql.DatabaseConnector;
import com.wurmcraft.server_essentials.rest.sql.TableChecker;
import io.javalin.Javalin;
import io.javalin.http.util.RedirectToLowercasePathPlugin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import org.reflections8.Reflections;
import org.reflections8.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SE_Rest {

    public static final Logger LOG = LoggerFactory.getLogger(SE_Rest.class);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Reflections REFLECTIONS = new Reflections("com.wurmcraft.server_essentials", new MethodAnnotationsScanner());

    public static Javalin javalin;
    public static RestConfig config;
    public static DatabaseConnector connector;

    public static void main(String[] args) {
        config = ConfigLoader.loadSafe(new File("."), new RestConfig());
        // Setup Swagger
        javalin = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(new OpenApiOptions(new Info().version("0.0.0").description("Server Essentials Rest API")).path("/swagger-docs").swagger(new SwaggerOptions("/swagger").title("Server Essentials Swagger"))));
            config.registerPlugin(new RedirectToLowercasePathPlugin());
        });
        javalin.config.precompressStaticFiles = true;
        javalin.config.showJavalinBanner = false;
        Routes.register(javalin);
        connector = new DatabaseConnector(config.database);
        TableChecker.createIfMissing();
        javalin.start(config.host, config.port);
    }
}
