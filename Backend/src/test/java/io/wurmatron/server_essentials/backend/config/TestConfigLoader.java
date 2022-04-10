/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.config;

import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.ServerEssentialsBackendTest;
import io.wurmatron.server_essentials.backend.model.config.BackendConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestConfigLoader {

  @BeforeAll
  public static void setup() {
    ServerEssentialsBackend.scheduledService = Executors.newScheduledThreadPool(4);
    ServerEssentialsBackend.backendConfiguration = null;
    deleteStorageFolder();
  }

  private static void deleteStorageFolder() {
    if (ServerEssentialsBackendTest.SAVE_DIR.exists()) {
      try {
        Files.walk(ServerEssentialsBackendTest.SAVE_DIR.toPath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @AfterAll
  public static void cleanup() {
    deleteStorageFolder();
  }

  @Test
  @Order(1)
  public void TestLoadBackendConfig_NonExistError() {
    try {
      BackendConfig backendConfig = ConfigLoader.loadBackendConfig(
          ServerEssentialsBackendTest.SAVE_DIR);
      fail("Loaded non-existing config file! (" + backendConfig + ")");
    } catch (IOException e) {
      assertTrue(true, ""); // Message will never be displayed
    }
  }

  @Test
  @Order(2)
  public void TestCreateBackendConfig() {
    try {
      Config backend = ConfigLoader.create(new BackendConfig(),
          ServerEssentialsBackendTest.SAVE_DIR);
      assertNotNull(backend, "Config is non-null");
    } catch (IOException e) {
      e.printStackTrace();
      fail("Failed to create backend config");
    }
  }

  @Test
  @Order(3)
  public void TestLoadBackendConfig_Load() throws IOException {
    BackendConfig backendConfig = ConfigLoader.loadBackendConfig(
        ServerEssentialsBackendTest.SAVE_DIR);
    assertNotNull(backendConfig);
  }

  @Test
  @Order(3)
  public void TestLoadBackendConfig_Update() throws IOException {
    BackendConfig newConfig = new BackendConfig();
    newConfig.General.fileResyncInterval = 900;
    ConfigLoader.save(newConfig, ServerEssentialsBackendTest.SAVE_DIR);
    BackendConfig loadedConfig = ConfigLoader.loadBackendConfig(
        ServerEssentialsBackendTest.SAVE_DIR);
    if (loadedConfig != null) {
      assertEquals(newConfig.General.fileResyncInterval,
          loadedConfig.General.fileResyncInterval);
    } else {
      fail("Failed to load updated config");
    }
  }
}
