package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;

public class RankUtils {

  /**
   * Checks if a rank is greater or equal than another via inheritance
   *
   * @param testRank rank to test against
   * @param rank provided rank
   */
  public static boolean isGreaterThan(Rank testRank, Rank rank) {
    if (testRank.name.equalsIgnoreCase(rank.name)) return true;
    for (String inh : testRank.inheritance) {
      if (inh.equalsIgnoreCase(rank.name)) return true;
      Rank tempRank = SECore.dataLoader.get(DataLoader.DataType.RANK, inh, new Rank());
      return isGreaterThan(tempRank, rank);
    }
    return false;
  }

  /**
   * Checks if a rank is greater or equal than another via inheritance
   *
   * @param testRank rank to test against
   * @param rank provided rank
   * @see #isGreaterThan(Rank, Rank)
   */
  public static boolean isGreaterThan(String testRank, String rank) {
    Rank test = SECore.dataLoader.get(DataLoader.DataType.RANK, testRank, new Rank());
    Rank user = SECore.dataLoader.get(DataLoader.DataType.RANK, rank, new Rank());
    if (test == null || user == null) {
      if (test == null)
        ServerEssentials.LOG.warn(
            "Failed to get rank '" + ((test == null) ? testRank : rank) + "'");
      return false;
    }
    return isGreaterThan(test, user);
  }

  /**
   * Checks if a rank is greater or equal than another via inheritance
   *
   * @param testRank rank to test against
   * @param ranks provided ranks
   * @see #isGreaterThan(String, String)
   */
  public static boolean isGreaterThan(String testRank, String[] ranks) {
    for (String rank : ranks) if (isGreaterThan(testRank, rank)) return true;
    return false;
  }

  // TODO Implement
  public static boolean hasPermission(Account account, String perm) {
    return true;
  }
}
