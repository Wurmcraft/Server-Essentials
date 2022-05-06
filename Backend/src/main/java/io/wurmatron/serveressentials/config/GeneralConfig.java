/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.config;

public class GeneralConfig {

  public boolean testing;

  public GeneralConfig(boolean testing) {
    this.testing = testing;
  }

  public GeneralConfig() {
    this.testing = false;
  }
}
