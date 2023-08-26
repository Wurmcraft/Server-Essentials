/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.utils;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.ServerEssentialsRest.SAVE_DIR;

import io.wurmatron.serveressentials.config.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import me.grison.jtoml.impl.Toml;

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
          throw new IOException(
              "Failed to delete " + configFile.getAbsolutePath() + "'!");
        }
      }
      LOG.error("Unable to access / load config file ('config.json')");
    } else { // New Config
      // Make sure config dir exists
      if (!SAVE_DIR.exists() && !SAVE_DIR.mkdirs()) {
        LOG.error("Failed to create dir '" + SAVE_DIR.getAbsolutePath() + "'");
        throw new IOException(
            "Failed to create dir '" + SAVE_DIR.getAbsolutePath() + "'");
      }
      // Create and save new instance
      config = askForConfiguration(new Config());
      String toml = FileUtils.toString(config, "toml");
      Files.write(
          configFile.toPath(),
          toml.getBytes(StandardCharsets.UTF_8),
          StandardOpenOption.CREATE_NEW,
          StandardOpenOption.WRITE);
      LOG.info("Default config created at '" + configFile.getAbsolutePath() + "'");
      return config;
    }
    return null;
  }

  private static final Scanner SCANNER = new Scanner(System.in);

  private static Config askForConfiguration(Config config) {
    System.out.println(
        "New installation detected, if not, copy the config.toml and /internal from your previous installation.");
    System.out.println();
    System.out.println("- General Setup");
    config.server.host = askQuestion("IP used to host the API from", "localhost");
    boolean set = false;
    while (!set) {
      try {
        config.server.port = Integer.parseInt(
            askQuestion("Port to run the API on", "8080"));
        set = true;
      } catch (NumberFormatException e) {
        System.out.println("Invalid Port, Must be a number!");
      }
    }
    // Configure database part of config
    System.out.println();
    System.out.println("- Database Setup");
    config.database.connector = askQuestion("What type of database 'postgresql', 'mysql'",
        "mysql");
    config.database.host = askQuestion(
        "IP / domain to connect to the {NAME} database".replaceAll("\\{NAME}",
            config.database.connector.equals("mysql") ? "mysql" : "postgres"),
        "localhost");
    set = false;
    while (!set) {
      try {
        config.database.port = Integer.parseInt(
            askQuestion("Port to connect to the database",
                config.database.host.equals("mysql") ? "3306" : "5432"));
        set = true;
      } catch (NumberFormatException e) {
        System.out.println("Invalid Port, Must be a number!");
      }
    }
    config.database.database = askQuestion("Name of the database to use",
        "server-essentials");
    config.database.username = askQuestion("Username to connect to the database",
        "serveressentials");
    config.database.password = askQuestion(
        "Password for {USER} on the database".replaceAll("\\{USER}",
            config.database.username), "");
    System.out.println();
    System.out.println("- Discord Bot Setup");
    config.discord.token = askQuestion("Token used by the bot to connect to discord", "");
    config.discord.verifiedRankID = askQuestion(
        "ID of the rank to give to users upon verification", "");
    return config;
  }

  public static String askQuestion(String question, String defaultVal) {
    System.out.print(question + " (" + defaultVal + ") > ");
    String nextLine = SCANNER.nextLine();
    if (nextLine == null || nextLine.isEmpty()) {
      return defaultVal;
    }
    return nextLine;
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
      DatabaseConfig dbConfig =
          new DatabaseConfig(
              toml.getString("config.database.username"),
              toml.getString("config.database.password"),
              Math.toIntExact(toml.getLong("config.database.port")),
              toml.getString("config.database.host"),
              toml.getString("config.database.database"),
              toml.getString("config.database.sqlParams"),
              toml.getString("config.database.connector"));
      GeneralConfig generalConfig = new GeneralConfig(
          toml.getBoolean("config.general.testing"),
          Math.toIntExact(toml.getLong("config.general.threads")));
      ServerConfig sererConfig =
          new ServerConfig(
              Math.toIntExact(toml.getLong("config.server.port")),
              toml.getString("config.server.host"),
              toml.getString("config.server.corosOrigins"),
              toml.getLong("config.server.requestTimeout"),
              toml.getBoolean("config.server.forceLowercase"),
              toml.getBoolean("config.server.swaggerEnabled"),
              Math.toIntExact(toml.getLong("config.server.cacheTime")),
              Math.toIntExact(toml.getLong("config.server.cleanupInterval")));
      DiscordConfig discordConfig =
          new DiscordConfig(
              toml.getString("config.discord.token"),
              toml.getMap("config.discord.channelMap"),
              toml.getString("config.discord.verifiedRankID"));
      return new Config(generalConfig, dbConfig, sererConfig, discordConfig);
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error("Failed to read toml config file");
      LOG.error(e.getMessage());
    }
    return null;
  }
}
