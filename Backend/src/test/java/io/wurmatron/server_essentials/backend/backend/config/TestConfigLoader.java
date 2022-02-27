/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.backend.config;

import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.server_essentials.backend.backend.ServerEssentialsBackendTest;
import io.wurmatron.server_essentials.backend.config.Config;
import io.wurmatron.server_essentials.backend.config.ConfigLoader;
import io.wurmatron.server_essentials.backend.model.config.BackendConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestConfigLoader {

  @AfterAll
  public static void setup() {
    try {
      Files.delete(ServerEssentialsBackendTest.SAVE_DIR.toPath());
    } catch (IOException e) {
      fail("Failed to delete '" + ServerEssentialsBackendTest.SAVE_DIR + "'");
    }
  }

  @Test
  @Order(1)
  public void TestLoadBackendConfig_NonExistError() {
    try {
      BackendConfig backendConfig = ConfigLoader.loadBackendConfig();
      fail("Loaded non-existing config file! (" + backendConfig + ")");
    } catch (IOException e) {
      assertTrue(true, ""); // Message will never be displayed
    }
  }

  @Test
  @Order(1)
  public void TestLoadBackendConfig_NonNull() throws IOException {
    BackendConfig backendConfig = ConfigLoader.loadBackendConfig();
    assertNotNull(backendConfig);
  }

  @Test
  @Order(1)
  public static void TestBackendConfig_Creation() throws IOException {
    Config newConfig = new BackendConfig();
    BackendConfig backendConfig =
        (BackendConfig) ConfigLoader.create(newConfig, ServerEssentialsBackendTest.SAVE_DIR);
    assertEquals(newConfig, backendConfig, "New configuration file should be the same");
  }

  @Test
  @Order(2)
  public void TestBackendConfig_FileTest() {
    File configFile =
        new File(ServerEssentialsBackendTest.SAVE_DIR + File.separator + "backend.toml");
    assertTrue(configFile.exists(), "backend.toml should exist after creation");
  }

  @Test
  @Order(2)
  public void TestBackendConfig_Modify() throws IOException {
    BackendConfig backendConfig = ConfigLoader.loadBackendConfig();
    assertNotNull(backendConfig, "Config to modify is not null");
    backendConfig.General.debug = !backendConfig.General.debug;
    File configFile = ConfigLoader.save(backendConfig, ServerEssentialsBackendTest.SAVE_DIR);
    assertNotNull(configFile, "Config file exists!");
  }

  @Test
  @Order(3)
  public void TestBackendConfig_Modified() throws IOException {
    BackendConfig backendConfig = ConfigLoader.loadBackendConfig();
    assertNotNull(backendConfig, "Config is not null");
    boolean defaultVal = new BackendConfig().General.debug;
    assertEquals(!defaultVal, backendConfig.General.debug, "Config value has been modified");
  }
}
