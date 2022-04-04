/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.config;

public interface Config {

    /**
     * Name of the file to be stored
     *
     * @return name of the file without a path or file extension
     */
    String getName();

    /**
     * Style / Format of the config
     */
    ConfigStyle getConfigStyle();

    /**
     * Used to load /set the config values and configure the system.
     *
     * @param isReloaded true if the config has been modified and is being reloaded
     */
    void setValues(boolean isReloaded);

    enum ConfigStyle {
        JSON, TOML
    }
}
