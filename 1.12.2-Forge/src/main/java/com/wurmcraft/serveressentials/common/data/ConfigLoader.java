package com.wurmcraft.serveressentials.common.data;

import com.wurmcraft.serveressentials.api.models.config.ConfigGlobal;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

public class ConfigLoader {

    public static final File SAVE_DIR = new File("Server-Essentials");
    public static final File GLOBAL_CONFIG = new File(SAVE_DIR + File.separator + "global.json");

    public static ConfigGlobal loadGlobalConfig() {
        if (GLOBAL_CONFIG.exists()) {
            try {
                ConfigGlobal config = GSON.fromJson(Strings.join(Files.readAllLines(GLOBAL_CONFIG.toPath()), '\n'), ConfigGlobal.class);
                LOG.info("Storage Type: '" + config.storage.storageType + "'");
                LOG.info("Debug Mode: " + config.general.debug);
                ConfigGlobal defaultConfig = new ConfigGlobal();
                if (defaultConfig.configVersion.equals(config.configVersion))
                    return config;
                else {
                    LOG.warn("Config version does not match the defaults, adding defaults!");
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
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            Files.write(file.toPath(), GSON.toJson(config).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Failed to save '" + file.getAbsolutePath() + "'");
        }
    }
}
