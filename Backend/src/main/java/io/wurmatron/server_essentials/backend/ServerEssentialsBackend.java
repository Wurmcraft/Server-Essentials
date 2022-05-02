/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.wurmatron.server_essentials.backend.cli.CommandRunner;
import io.wurmatron.server_essentials.backend.config.InitialStartupConfigurator;
import io.wurmatron.server_essentials.backend.model.config.BackendConfig;
import io.wurmatron.server_essentials.backend.rest.JavalinConfigurator;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerEssentialsBackend {

  // Global
  public static final Logger LOG = LoggerFactory.getLogger(ServerEssentialsBackend.class);
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static final File SAVE_DIR = new File("Server-Essentials");
  // Set on runtime
  public static final String VERSION = "@VERSION@";
  //
  public static BackendConfig backendConfiguration;
  public static Javalin javalin;
  public static ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(
      4);

  public static void main(String[] args) {
    displaySystemInfo();
    if (!InitialStartupConfigurator.loadOrSetup(args)) {
      ServerEssentialsBackend.LOG.error(
          "Failed to initialization! Please check the logs for more details");
    }
    javalin = JavalinConfigurator.setupAndConfigure(backendConfiguration, args);
    CommandRunner.handleCommands();
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
