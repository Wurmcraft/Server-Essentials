package io.wurmatron.serveressentials.utils;

import io.wurmatron.serveressentials.config.*;
import me.grison.jtoml.impl.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.ServerEssentialsRest.SAVE_DIR;

/**
 * Handles everything to do with config's (loading, saving, errors)
 */
public class ConfigLoader {

    /**
     * Called to load the main config
     *
     * @return Instance of config loaded, from config.toml with in the main SE directory
     */
    public static Config setupAndHandleConfig() throws IOException {
        Config config;
        File configFile = new File(SAVE_DIR + File.separator + "config.toml");
        if (SAVE_DIR.exists() && configFile.exists()) { // Existing
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
                  throw new IOException("Failed to delete " + configFile.getAbsolutePath() + "'!");
                }
            }
            LOG.error("Unable to access / load config file ('config.json')");
        } else {    // New Config
            // Make sure config dir exists
            if (!SAVE_DIR.exists() && !SAVE_DIR.mkdirs()) {
                LOG.error("Failed to create dir '" + SAVE_DIR.getAbsolutePath() + "'");
                throw new IOException("Failed to create dir '" + SAVE_DIR.getAbsolutePath() + "'");
            }
            // Create and save new instance
            config = new Config();
            String toml = FileUtils.toString(config, "toml");
            Files.write(configFile.toPath(), toml.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            LOG.info("Default config created at '" + configFile.getAbsolutePath() + "'");
            LOG.error("Unable to save config file ('config.json')");
        }
        return null;
    }

    /**
     * Load a config from a instance of toml
     *
     * @param toml instance of toml get collect the data from
     * @return instance of the config, created from the toml instance
     * @see Toml#parse(File)
     */
    private static Config readConfigFromTOML(Toml toml) {
        try {
            // Read Database
            DatabaseConfig dbConfig = new DatabaseConfig(
                    toml.getString("config.database.username"),
                    toml.getString("config.database.password"),
                    Math.toIntExact(toml.getLong("config.database.port")),
                    toml.getString("config.database.host"),
                    toml.getString("config.database.database"),
                    toml.getString("config.database.sqlParams"),
                    toml.getString("config.database.connector"));
            GeneralConfig generalConfig = new GeneralConfig(
                    toml.getBoolean("config.general.testing"));
            ServerConfig sererConfig = new ServerConfig(
                    Math.toIntExact(toml.getLong("config.server.port")),
                    toml.getString("config.server.host"),
                    toml.getString("config.server.corosOrigins"),
                    toml.getLong("config.server.requestTimeout"),
                    toml.getBoolean("config.server.forceLowercase"),
                    toml.getBoolean("config.server.swaggerEnabled"),
                    Math.toIntExact(toml.getLong("config.server.cacheTime")));
            DiscordConfig discordConfig = new DiscordConfig(toml.getString("config.discord.token"), toml.getMap("config.discord.channelMap"));
            return new Config(generalConfig, dbConfig, sererConfig, discordConfig);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Failed to read toml config file");
            LOG.error(e.getMessage());
        }
        return null;
    }
}
