/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.tests.sql;

import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAutoRank;
import io.wurmatron.serveressentials.sql.routes.SQLCacheRank;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import java.util.List;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAutoRanks {

  public static final AutoRank TEST_AUTORANK =
      new AutoRank("Test", "Test2", 500, "TesT", 500, "{}");

  @BeforeAll
  public static void setup() throws Exception {
    ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
    ServerEssentialsRest.dbConnection = SQLGenerator.create();
  }

  @Test
  @Order(1)
  public void testAddAutoRank() {
    AutoRank addedAutoRank = SQLCacheAutoRank.create(TEST_AUTORANK);
    assertNotNull(addedAutoRank, "AutoRank has been successfully created without errors");
    // Check for Auto Rank
    addedAutoRank = SQLCacheAutoRank.get(TEST_AUTORANK.rank);
    assertNotNull(addedAutoRank, "Auto-Rank Exists");
    assertEquals(TEST_AUTORANK.rank, addedAutoRank.rank, "Auto-Ranks should be the same");
  }

  @Test
  @Order(2)
  public void testGetAutoRank() {
    AutoRank autorank = SQLCacheAutoRank.get(TEST_AUTORANK.rank);
    assertNotNull(autorank, "Auto-Rank exists");
    // Remove from cache and try again
    SQLCacheAutoRank.invalidate(TEST_AUTORANK.rank);
    autorank = SQLCacheAutoRank.get(TEST_AUTORANK.rank);
    assertNotNull(autorank, "Auto-Rank exists");
    assertEquals(TEST_AUTORANK.playtime, autorank.playtime, "Auto-Rank is the same");
  }

  @Test
  @Order(2)
  public void testGetAutoRanks() {
    List<AutoRank> autoranks = SQLCacheAutoRank.get();
    assertTrue(autoranks.size() > 0, "Auto-Ranks exist");
    // Remove from cache and try again
    for (AutoRank ar : autoranks) SQLCacheRank.invalidate(ar.rank);
    autoranks = SQLCacheAutoRank.get();
    assertTrue(autoranks.size() > 0, "Auto-Ranks exist");
    boolean found = false;
    for (AutoRank ar : autoranks)
      if (ar.equals(TEST_AUTORANK)) {
        found = true;
        break;
      }
    assertTrue(found, "Added auto-rank exists");
  }

  @Test
  @Order(2)
  public void testUpdateAutoRank() {
    TEST_AUTORANK.playtime = 800L;
    boolean updated = SQLCacheAutoRank.update(TEST_AUTORANK, new String[] {"playtime"});
    assertTrue(updated, "Auto-Rank has been updated updated without errors");
    AutoRank autoRank = SQLCacheAutoRank.get(TEST_AUTORANK.rank);
    assertEquals(TEST_AUTORANK.playtime, autoRank.playtime, "AutoRank is the same");
    // Remove from cache and try again
    autoRank = SQLCacheAutoRank.get(TEST_AUTORANK.rank);
    assertEquals(TEST_AUTORANK.rank, autoRank.rank, "Auto-Rank is the same");
  }

  @Test
  @Order(3)
  public void testDeleteAutoRank() {
    boolean deleted = SQLCacheAutoRank.delete(TEST_AUTORANK.rank);
    assertTrue(deleted, "Auto-Rank has been successfully deleted without errors");
    // Make sure it was deleted
    AutoRank autoRank = SQLCacheAutoRank.get(TEST_AUTORANK.rank);
    assertNull(autoRank, "Auto-Rank does not exist");
    // Invalidate Cache and try again
    SQLCacheAutoRank.invalidate(TEST_AUTORANK.rank);
    autoRank = SQLCacheAutoRank.get(TEST_AUTORANK.rank);
    assertNull(autoRank, "Auto-Rank does not exist");
  }
}
