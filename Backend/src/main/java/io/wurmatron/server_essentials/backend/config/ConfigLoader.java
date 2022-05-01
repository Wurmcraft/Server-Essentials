/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.config;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.config.Config.ConfigStyle;
import io.wurmatron.server_essentials.backend.io.FileWatcher;
import io.wurmatron.server_essentials.backend.model.config.BackendConfig;
import me.grison.jtoml.impl.Toml;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;

import static io.wurmatron.server_essentials.backend.ServerEssentialsBackend.GSON;

/**
 * Helps with the loading of configurations from multiple formats and locations
 */
public class ConfigLoader {

  // Stores the loaded config's as a cache
  private static final NonBlockingHashMap<String, Config> configCache = new NonBlockingHashMap<>();
  private static final NonBlockingHashMap<String, FileWatcher> fileWatchers = new NonBlockingHashMap<>();

  /**
   * Loads the config for the backend in TOML format from the SAVE_DIR/backend.toml
   *
   * @throws IOException backend.toml cannot be found
   */
  public static BackendConfig loadBackendConfig(File saveDir) throws IOException {
    BackendConfig newInstance = new BackendConfig();
    Config backendConfig = load(newInstance, saveDir);
    if (backendConfig instanceof BackendConfig) {
      return (BackendConfig) backendConfig;
    }
    ServerEssentialsBackend.LOG.warn("Failed to load backend.json!");
    ServerEssentialsBackend.LOG.debug("config instance is not of type BackendConfig");
    return null;
  }

  public static BackendConfig loadOrCreateBackendConfig(File saveDir) throws IOException {
    if (!new File(saveDir + File.separator + "backend.json").exists()) {
      ConfigLoader.create(new BackendConfig(), ServerEssentialsBackend.SAVE_DIR);
    }
    return ConfigLoader.loadBackendConfig(saveDir);
  }

  /**
   * Create a new configuration, based on the provided instance
   *
   * @param config instance of the config to be saved
   * @param saveDir directory to save the config file in
   * @throws IOException Any write / file creation errors
   */
  public static Config create(Config config, File saveDir) throws IOException {
    File configFile = new File(
        saveDir + File.separator + config.getName() + "." + config.getConfigStyle().name()
            .toLowerCase());
    if (!configFile.exists()) {
      if (!configFile.getParentFile().exists()) {
        configFile.getParentFile().mkdirs();
      }
      // Make sure the file is created
      if (configFile.createNewFile()) {
        // Save to the file and reload
        configFile = save(config, saveDir);
        if (configFile != null) {
          return load(config, saveDir);
        }
      } else {
        throw new IOException("Config File '" + configFile.getAbsolutePath()
            + "' has failed to be created!");
      }
    } else {
      throw new IOException("File '" + configFile.getAbsolutePath() + "' exists!");
    }
    return null;
  }

  /**
   * Loads a config file based on its instance and save directory
   *
   * @param configInstance instance of the config to be loaded
   * @param saveDir directory with the saved config file
   * @return instance of the config loaded from the file
   * @throws IOException Unable to find / load the provided config instance
   */
  public static <T extends Config> T load(T configInstance, File saveDir)
      throws IOException {
    File configFile = new File(saveDir + File.separator + configInstance.getName() + "."
        + configInstance.getConfigStyle().name().toLowerCase());
    if (configFile.exists()) {
      // Read File into Instance
      if (configInstance.getConfigStyle().equals(Config.ConfigStyle.JSON)) {
        configInstance = (T) GSON.fromJson(new FileReader(configFile),
            configInstance.getClass());
      } else if (configInstance.getConfigStyle().equals(Config.ConfigStyle.TOML)) {
        configInstance = (T) Toml.parse(configFile).getAs("", configInstance.getClass());
      } else if (configInstance.getConfigStyle().equals(Config.ConfigStyle.TXT)) {
        configInstance = TextFileConfigReaderAndLoader.load(configInstance, configFile);
      }
      // Write to cache and setup tracking
      configCache.put(configInstance.getName(), configInstance);
      Config finalConfigInstance = configInstance;
      getWatcher(configInstance, saveDir).track((kind, filename, bak) -> {
        try {
          finalConfigInstance.setValues(
              kind.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY));
          ServerEssentialsBackend.LOG.info(
              finalConfigInstance.getName() + "."
                  + finalConfigInstance.getConfigStyle().name()
                  .toLowerCase() + " has been reloaded!");
        } catch (Exception e) {
          ServerEssentialsBackend.LOG.warn(
              "Failed to reload " + finalConfigInstance.getName() + "."
                  + finalConfigInstance.getConfigStyle().name()
                  .toLowerCase());
          // TODO Handle Config Reload Error
        }
      }, configInstance.getName() + "." + configInstance.getConfigStyle().name()
          .toLowerCase(), false, FileWatcher.BackupMode.DELETE_ON_COMPLETE);
      return configInstance;
    } else {
      throw new IOException(
          "File '" + configFile.getAbsolutePath() + "' does not exist!");
    }
  }

  /**
   * Save a config instance into a file
   *
   * @param instance Object instance to be saved.
   * @param saveDir directory the config will be saved within
   * @return the file that the config was saved as
   * @throws IOException Unable to save / write the config file
   */
  public static File save(Config instance, File saveDir) throws IOException {
    // Convert instance to string
    String fileBytes = "";
    if (instance.getConfigStyle().equals(Config.ConfigStyle.JSON)) {
      fileBytes = GSON.toJson(instance);
    } else if (instance.getConfigStyle().equals(Config.ConfigStyle.TOML)) {
      fileBytes = Toml.serialize(instance);
    } else if(instance.getConfigStyle().equals(ConfigStyle.TXT)) {
      fileBytes = TextFileConfigReaderAndLoader.convertToString(instance);
    }
    File configFile = new File(
        saveDir + File.separator + instance.getName() + "." + instance.getConfigStyle()
            .name().toLowerCase());
    if (configFile.exists()) {
      Files.write(configFile.toPath(), fileBytes.getBytes(), StandardOpenOption.WRITE);
      Config loadedConfig = load(instance, saveDir);
      if (loadedConfig != null) {
        return configFile;
      }
    } else {
      throw new IOException(
          "File '" + configFile.getAbsolutePath() + "' does not exist!");
    }
    return null;
  }

  /**
   * Get the file watcher for a specific config file / directory
   *
   * @param config instance of the config that is being tracked
   */
  private static FileWatcher getWatcher(Config config, File saveDir) throws IOException {
    if (config.getName().contains(File.separator)) {
      // Load / Track non-base directory
      String dir = config.getName()
          .substring(0, config.getName().indexOf(File.separator));
      if (fileWatchers.containsKey(dir)) {
        return fileWatchers.get(dir);
      } else {
        FileWatcher watcher = new FileWatcher(FileSystems.getDefault().newWatchService(),
            new File(saveDir + File.separator + dir));
        fileWatchers.put(dir, watcher);
        ServerEssentialsBackend.LOG.debug(
            "Creating file watcher for directory'" + saveDir
                + "/'");
        return getWatcher(config, saveDir);
      }
    } else {
      // Base loader / directory
      if (fileWatchers.containsKey("/")) {
        return fileWatchers.get("/");
      } else { // Create base file watcher
        FileWatcher watcher = new FileWatcher(FileSystems.getDefault().newWatchService(),
            saveDir);
        fileWatchers.put("/", watcher);
        ServerEssentialsBackend.LOG.debug(
            "Creating file watcher for directory'" + saveDir
                + "/'");
        return getWatcher(config, saveDir);
      }
    }
  }
}
