package io.wurmatron.serveressentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wurmatron.serveressentials.config.Config;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ServerEssentialsRest {

    public static final Logger LOG = LoggerFactory.getLogger(ServerEssentialsRest.class);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File SAVE_DIR = new File("Server-Essentials");

    public static Config config;

    public static void main(String[] args) {
        displaySystemInfo();
        config = ConfigLoader.setupAndHandleConfig();
    }

    public static void displaySystemInfo() {
        LOG.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        LOG.debug("OS: " + System.getProperty("os.name") + "-" + System.getProperty("os.arch"));
        LOG.debug("CPU: " + Runtime.getRuntime().availableProcessors() + " cores");
        LOG.debug("Java: " + System.getProperty("java.runtime.version"));
        LOG.debug("Memory: " + (Runtime.getRuntime().totalMemory() / 1000000) + "MB | MAX: " + (Runtime.getRuntime().maxMemory() / 1000000) + "MB");
        LOG.debug("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }
}
