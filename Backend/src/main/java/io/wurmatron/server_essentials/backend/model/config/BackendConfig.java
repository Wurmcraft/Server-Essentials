/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.config;

import io.wurmatron.server_essentials.backend.config.Config;

public class BackendConfig implements Config {

  public General General;

  // Defaults
  public BackendConfig() {
    this.General = new General(false, 30);
  }

  public BackendConfig(BackendConfig.General general) {
    General = general;
  }

  public static class General {

    public boolean debug;
    public int fileResyncInterval;

    public General(boolean debug, int fileResyncInterval) {
      this.debug = debug;
      this.fileResyncInterval = fileResyncInterval;
    }
  }

  @Override
  public String getName() {
    return "backend";
  }

  @Override
  public ConfigStyle getConfigStyle() {
    return ConfigStyle.JSON;
  }

  @Override
  public void setValues(boolean isReloaded) {
    // TODO Implement config loading
  }
}
