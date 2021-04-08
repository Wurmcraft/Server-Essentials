package io.wurmatron.serveressentials.utils;

import io.wurmatron.serveressentials.config.Config;
import io.wurmatron.serveressentials.config.DatabaseConfig;
import io.wurmatron.serveressentials.config.GeneralConfig;
import io.wurmatron.serveressentials.config.ServerConfig;
import me.grison.jtoml.impl.Toml;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.ServerEssentialsRest.SAVE_DIR;

public class ConfigLoader {

    public static Config setupAndHandleConfig() {
        Config config;
        File configFile = new File(SAVE_DIR + File.separator + "config.toml");
        if (SAVE_DIR.exists() && configFile.exists()) { // Existing
            try {
                Toml toml = Toml.parse(configFile);
                config = readConfigFromTOML(toml);
                if (config != null) {
                    LOG.info("Loaded Config file '" + configFile.getAbsolutePath() + "'");
                    return config;
                } else {
                    if (configFile.delete()) {
                        LOG.info("Empty Config File, Deleting...");
                        LOG.info("Attempting to recreate");
                        setupAndHandleConfig();
                    } else {
                        LOG.error("Failed to delete " + configFile.getAbsolutePath() + "'!");
                        System.exit(1);
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
                LOG.error("Unable to access / load config file ('config.json')");
                System.exit(1);
            }
        } else {    // New Config
            try {
                // Make sure config dir exists
                if (!SAVE_DIR.exists()) {
                    if (!SAVE_DIR.mkdirs()) {
                        LOG.error("Failed to create dir '" + SAVE_DIR.getAbsolutePath() + "'");
                        System.exit(1);
                    }
                }
                config = new Config();
                String toml = FileUtils.toString(config, "toml");
                Files.write(configFile.toPath(), toml.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                LOG.info("Default config created at '" + configFile.getAbsolutePath() + "'");
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
                LOG.error("Unable to save config file ('config.json')");
                System.exit(1);
            }
        }
        return null;
    }

    private static Config readConfigFromTOML(Toml toml) {
        try {
            // Read Database
            DatabaseConfig dbConfig = new DatabaseConfig(
                    toml.getString("config.database.username"),
                    toml.getString("config.database.password"),
                    Math.toIntExact(toml.getLong("config.database.port")),
                    toml.getString("config.database.host"));
            GeneralConfig generalConfig = new GeneralConfig();
            ServerConfig sererConfig = new ServerConfig(Math.toIntExact(toml.getLong("config.server.port")));
            return new Config(generalConfig, dbConfig, sererConfig);
        } catch (Exception e) {
            LOG.error("Failed to read toml config file");
            LOG.error(e.getMessage());
        }
        return null;
    }
}
