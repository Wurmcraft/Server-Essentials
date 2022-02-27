/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.config;

import io.wurmatron.server_essentials.backend.model.config.BackendConfig;
import java.io.File;
import java.io.IOException;

/** Helps with the loading of configurations from multiple formats and locations */
public class ConfigLoader {

  /**
   * TODO Implement
   *
   * <p>Loads the config for the backend in TOML format from the SAVE_DIR/backend.toml
   *
   * @throws IOException backend.toml cannot be found
   */
  public static BackendConfig loadBackendConfig() throws IOException {
    return null;
  }

  /**
   * TODO Implement
   *
   * <p>Create a new configuration, based on the provided instance
   *
   * @param config instance of the config to be saved
   * @param saveDir directory to save the config file in
   * @throws IOException Any write / file creation errors
   */
  public static Config create(Config config, File saveDir) throws IOException {
    return null;
  }

  /**
   * TODO Implement
   *
   * <p>Loads a config file based on its instance and save directory
   *
   * @param configInstance instance of the config to be loaded
   * @param saveDir directory with the saved config file
   * @return instance of the config loaded from the file
   * @throws IOException Unable to find / load the provided config instance
   */
  public static Config load(Config configInstance, File saveDir) throws IOException {
    return null;
  }

  /**
   * TODO Implement
   *
   * <p>Save a config instance into a file
   *
   * @param instance Object instance to be saved.
   * @param saveDir directory the config will be saved within
   * @return the file that the config was saved as
   * @throws IOException Unable to save / write the config file
   */
  public static File save(Config instance, File saveDir) throws IOException {
    return null;
  }
}
