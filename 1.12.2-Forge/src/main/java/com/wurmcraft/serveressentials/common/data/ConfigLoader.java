package com.wurmcraft.serveressentials.common.data;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;
import com.wurmcraft.serveressentials.api.models.config.ConfigGlobal;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class ConfigLoader {

  public static final File SAVE_DIR = new File("Server-Essentials");
  public static final File GLOBAL_CONFIG = new File(SAVE_DIR + File.separator + "global.json");

  public static ConfigGlobal loadGlobalConfig() {
    if (GLOBAL_CONFIG.exists()) {
      try {
        ConfigGlobal config =
            GSON.fromJson(
                Strings.join(Files.readAllLines(GLOBAL_CONFIG.toPath()), '\n'), ConfigGlobal.class);
        LOG.info("Storage Type: '" + config.storage.storageType + "'");
        LOG.info("Debug Mode: " + config.general.debug);
        ConfigGlobal defaultConfig = new ConfigGlobal();
        if (defaultConfig.configVersion.equals(config.configVersion)) {
          return config;
        } else {
          LOG.warn("Config version does not match the defaults, adding defaults!");
          config.configVersion = defaultConfig.configVersion;
          save(GLOBAL_CONFIG, config);
          return config;
        }
      } catch (IOException e) {
        e.printStackTrace();
        LOG.error("Failed to read '" + GLOBAL_CONFIG.getAbsolutePath() + "'");
      }
    } else {
      ConfigGlobal global = new ConfigGlobal();
      save(GLOBAL_CONFIG, global);
      LOG.info("Global defaults have been set");
      return global;
    }
    return null;
  }

  public static void save(File file, Object config) {
    try {
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      Files.write(
          file.toPath(),
          GSON.toJson(config).getBytes(),
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("Failed to save '" + file.getAbsolutePath() + "'");
    }
  }

  public static NonBlockingHashMap<String, Object> loadModuleConfigs() {
    List<Object> moduleConfigs = AnnotationLoader.loadModuleConfigs();
    NonBlockingHashMap<String, Object> instances = new NonBlockingHashMap<>();
    for (Object configInstance : moduleConfigs) {
      String moduleName =
          configInstance.getClass().getDeclaredAnnotation(ModuleConfig.class).module();
      if (!moduleName.isEmpty()) {
        instances.put(moduleName.toUpperCase(), loadModuleConfig(moduleName, configInstance));
      }
    }
    LOG.info(moduleConfigs.size() + " module config(s) have been loaded");
    return instances;
  }

  /**
   * Loads a module, creating a new config file if needed
   *
   * @param moduleName name of the module / name of the config file
   * @param configInstance default instance of the config module instance
   */
  public static Object loadModuleConfig(String moduleName, Object configInstance) {
    File configFile =
        new File(SAVE_DIR + File.separator + "Modules" + File.separator + moduleName + ".json");
    if (configFile.exists()) {
      try {
        List<String> json = Files.readAllLines(configFile.toPath());
        return GSON.fromJson(
            String.join("\n", json.toArray(new String[0])), configInstance.getClass());
      } catch (IOException e) {
        e.printStackTrace();
        LOG.warn(
            "Failed to load module config '"
                + moduleName
                + "' ("
                + configFile.getAbsolutePath()
                + ")");
      }
    } else {
      if (!configFile.getParentFile().exists()) {
        if (!configFile.getParentFile().mkdirs()) {
          LOG.warn(
              "Failed to create directory for module config's ("
                  + configFile.getParentFile().getAbsolutePath()
                  + ")");
        }
      }
      try {
        if (configFile.createNewFile()) {
          Files.write(configFile.toPath(), Collections.singleton(GSON.toJson(configInstance)));
          LOG.debug(
              "Creating default config file for module '"
                  + moduleName
                  + "' ("
                  + configFile.getAbsolutePath()
                  + ")");
        }
      } catch (IOException e) {
        e.printStackTrace();
        LOG.warn(
            "Failed to create module '"
                + moduleName
                + "' config ("
                + configFile.getAbsolutePath()
                + ")");
      }
    }
    return configInstance;
  }
}
