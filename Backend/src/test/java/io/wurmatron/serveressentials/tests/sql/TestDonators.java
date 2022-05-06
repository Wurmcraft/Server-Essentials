/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.tests.sql;

import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Donator;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAccount;
import io.wurmatron.serveressentials.sql.routes.SQLCacheDonator;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import java.time.Instant;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDonators {

  public static final Donator TEST_DONATOR =
      new Donator(
          "test",
          "1234",
          55.99,
          TestAccounts.TEST_ACCOUNT.uuid,
          Instant.now().getEpochSecond(),
          "Rank",
          "{\"rank\": \"Debug\"}");

  @BeforeAll
  public static void setup() throws Exception {
    ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
    ServerEssentialsRest.dbConnection = SQLGenerator.create();
  }

  @Test
  @Order(1)
  public void testAddDonator() {
    // Add new Donator
    Donator donator = SQLCacheDonator.newDonator(TEST_DONATOR);
    assertNotNull(donator, "Donator has been successfully created without errors");
    // Check for new donator
    Donator savedDonator = SQLCacheDonator.getDonator(TEST_DONATOR.uuid);
    assertEquals(
        TEST_DONATOR.uuid, savedDonator.uuid, "Added Donator should be the same as the one saved.");
  }

  @Test
  @Order(2)
  public void testGetDonator() {
    Donator donator = SQLCacheDonator.getDonator(TEST_DONATOR.uuid);
    assertEquals(TEST_DONATOR.amount, donator.amount, "The Donators are equal");
    // Remove from cache and try again
    SQLCacheDonator.invalidate(TEST_DONATOR.uuid);
    donator = SQLCacheDonator.getDonator(TEST_DONATOR.uuid);
    // Test Donator
    assertNotNull(donator, "Cache has the Donator");
    assertEquals(
        TEST_DONATOR.type, donator.type, "Added Donator should be the same as the one saved.");
  }

  @Test
  @Order(2)
  public void testUpdateDonator() {
    TEST_DONATOR.amount = 45.0;
    boolean updated = SQLCacheDonator.updateDonator(TEST_DONATOR, new String[] {"amount"});
    assertTrue(updated, "Donator has been successfully updated without errors");
    // Check for updates
    Donator donator = SQLCacheDonator.getDonator(TEST_DONATOR.uuid);
    assertNotNull(donator, "Donator Exists");
    assertEquals(TEST_DONATOR.amount, donator.amount, "Amount has been updated");
    // Invalidate Cache and try again
    SQLCacheAccount.invalidate(TEST_DONATOR.uuid);
    donator = SQLCacheDonator.getDonator(TEST_DONATOR.uuid);
    assertNotNull(donator, "Donator Exists");
    assertEquals(TEST_DONATOR.amount, donator.amount, "Amount has been updated");
  }

  @Test
  @Order(3)
  public void testDeleteDonator() {
    boolean deleted = SQLCacheDonator.deleteDonator(TEST_DONATOR.uuid);
    assertTrue(deleted, "Donator has been successfully deleted without errors");
    // Make sure it was deleted
    Donator donator = SQLCacheDonator.getDonator(TEST_DONATOR.uuid);
    assertNull(donator, "Donator does not exist");
    // Invalidate Cache and try again
    SQLCacheDonator.invalidate(TEST_DONATOR.uuid);
    donator = SQLCacheDonator.getDonator(TEST_DONATOR.uuid);
    assertNull(donator, "Donator does not exist");
  }
}
