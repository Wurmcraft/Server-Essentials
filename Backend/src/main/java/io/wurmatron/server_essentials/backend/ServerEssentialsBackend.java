/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.wurmatron.server_essentials.backend.model.config.BackendConfig;
import java.io.File;
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

  public static void main(String[] args) {}
}
