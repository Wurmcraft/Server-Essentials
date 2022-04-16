/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.config;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.config.Config;
import java.util.concurrent.Executors;

public class BackendConfig implements Config {

  public General General;

  // Defaults
  public BackendConfig() {
    this.General = new General(false, 30,
        Math.max((Runtime.getRuntime().availableProcessors() / 2), 1));
  }

  public BackendConfig(BackendConfig.General general) {
    General = general;
  }

  public static class General {

    public boolean debug;
    public int fileResyncInterval;
    public int threadPoolSize;

    public General(boolean debug, int fileResyncInterval, int threadPoolSize) {
      this.debug = debug;
      this.fileResyncInterval = fileResyncInterval;
      this.threadPoolSize = threadPoolSize;
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
    ServerEssentialsBackend.scheduledService = Executors.newScheduledThreadPool(
        General.threadPoolSize);
  }
}
