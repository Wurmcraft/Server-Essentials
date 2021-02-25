package com.wurmcraft.server_essentials.rest.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static com.wurmcraft.server_essentials.rest.SE_Rest.GSON;
import static com.wurmcraft.server_essentials.rest.SE_Rest.LOG;

public class ConfigLoader {

    public static <T extends Config> T load(File configDir, T type) {
        try {
            return GSON.fromJson(new FileReader(configDir + File.separator + type.getFileName()), (Class<T>) type.getClass());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static <T extends Config> void save(File configDir, T config) throws IOException {
        String data = GSON.toJson(config);
        File configFile = new File(configDir + File.separator + config.getFileName());
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
        LOG.trace("Attempting to save config to '" + configFile.getAbsolutePath() + "'");
        Files.write(
                configFile.toPath(), data.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    public static <T extends Config> T loadSafe(File configFile, T type) {
        try {
            T configInstance = load(configFile, type);
            if (configInstance != null) return configInstance;
        } catch (Exception e) {
            LOG.trace(e.getMessage());
        }
        if (!new File(configFile + File.separator + type.getFileName()).exists()) {
            try {
                save(configFile, type);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }
        return type;
    }
}
