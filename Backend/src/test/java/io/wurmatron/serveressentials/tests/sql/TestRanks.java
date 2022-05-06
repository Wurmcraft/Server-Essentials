/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.tests.sql;

import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheRank;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import java.util.List;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRanks {

  public static final Rank TEST_RANK =
      new Rank(
          "test",
          new String[] {"general.*", "language.*"},
          new String[] {"Test"},
          "[Test]",
          0,
          "&c",
          0,
          "",
          0);

  @BeforeAll
  public static void setup() throws Exception {
    ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
    ServerEssentialsRest.dbConnection = SQLGenerator.create();
  }

  @Test
  @Order(1)
  public void testAddRank() {
    Rank addedRank = SQLCacheRank.create(TEST_RANK);
    assertNotNull(addedRank, "Rank has been successfully created without errors");
    addedRank = SQLCacheRank.get(TEST_RANK.name);
    assertNotNull(addedRank, "Rank exists");
    assertEquals(TEST_RANK.name, addedRank.name, "Rank should be the same");
  }

  @Test
  @Order(2)
  public void testGetRank() {
    Rank rank = SQLCacheRank.get(TEST_RANK.name);
    assertNotNull(rank, "Rank exists");
    // Remove from cache and try again
    SQLCacheRank.invalidate(TEST_RANK.name);
    rank = SQLCacheRank.get(TEST_RANK.name);
    assertNotNull(rank, "Rank exists");
    assertEquals(TEST_RANK.prefix, rank.prefix, "Rank is the same");
  }

  @Test
  @Order(2)
  public void testGetRanks() {
    List<Rank> ranks = SQLCacheRank.get();
    assertTrue(ranks.size() > 0, "Ranks exist");
    // Remove from cache and try again
    for (Rank rank : ranks) SQLCacheRank.invalidate(rank.name);
    ranks = SQLCacheRank.get();
    assertTrue(ranks.size() > 0, "Ranks exist");
    boolean found = false;
    for (Rank rank : ranks)
      if (rank.name.equals(TEST_RANK.name)) {
        found = true;
        break;
      }
    assertTrue(found, "Added rank exists");
  }

  @Test
  @Order(2)
  public void testUpdateRank() {
    TEST_RANK.color = "Red";
    boolean updated = SQLCacheRank.update(TEST_RANK, new String[] {"color"});
    assertTrue(updated, "Rank has been updated updated without errors");
    Rank rank = SQLCacheRank.get(TEST_RANK.name);
    assertEquals(TEST_RANK.color, rank.color, "Rank is the same");
    // Remove from cache and try again
    rank = SQLCacheRank.get(TEST_RANK.name);
    assertEquals(TEST_RANK.color, rank.color, "Rank is the same");
  }

  @Test
  @Order(3)
  public void testDeleteRank() {
    boolean deleted = SQLCacheRank.delete(TEST_RANK.name);
    assertTrue(deleted, "Rank has been successfully deleted without errors");
    // Make sure it was deleted
    Rank rank = SQLCacheRank.get(TEST_RANK.name);
    assertNull(rank, "Rank does not exist");
    // Invalidate Cache and try again
    SQLCacheRank.invalidate(TEST_RANK.name);
    rank = SQLCacheRank.get(TEST_RANK.name);
    assertNull(rank, "Rank does not exist");
  }
}
