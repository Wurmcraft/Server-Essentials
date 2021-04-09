package io.wurmatron.serveressentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.http.util.RedirectToLowercasePathPlugin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import io.wurmatron.serveressentials.config.Config;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ServerEssentialsRest {

    public static final Logger LOG = LoggerFactory.getLogger(ServerEssentialsRest.class);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File SAVE_DIR = new File("Server-Essentials");

    public static Config config;
    public static Javalin javalin;

    public static void main(String[] args) {
        displaySystemInfo();
        config = ConfigLoader.setupAndHandleConfig();
        javalin = Javalin.create((cfg) -> {
            // Config
            cfg.precompressStaticFiles = true;
            cfg.defaultContentType = "application/json";
            cfg.autogenerateEtags = true;
            cfg.showJavalinBanner = false;
            if(!config.server.corosOrigins.isEmpty() )
            cfg.enableCorsForOrigin(config.server.corosOrigins.split(","));
            cfg.asyncRequestTimeout = config.server.requestTimeout;
            cfg.enforceSsl = !config.general.testing;
            // Plugins
            if (config.server.forceLowercase)
                cfg.registerPlugin(new RedirectToLowercasePathPlugin());
            if (config.server.swaggerEnabled)
                cfg.registerPlugin(new OpenApiPlugin(new OpenApiOptions(new Info().version("@VERSION@").description("Server Essentials Rest API")).path("/swagger-json").swagger(new SwaggerOptions("/swagger").title("Server-Essentials Swagger"))));
        });
        javalin.start(config.server.host.isEmpty() ? null : config.server.host, config.server.port);
    }

    public static void displaySystemInfo() {
        LOG.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        LOG.debug("OS: " + System.getProperty("os.name") + "-" + System.getProperty("os.arch"));
        LOG.debug("CPU: " + Runtime.getRuntime().availableProcessors() + " cores");
        LOG.debug("Java: " + System.getProperty("java.runtime.version"));
        LOG.debug("Memory: " + (Runtime.getRuntime().totalMemory() / 1000000) + "MB | MAX: " + (Runtime.getRuntime().maxMemory() / 1000000) + "MB");
        LOG.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }
}
