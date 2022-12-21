package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.security.TrustedList;

public class RankUtils {

  /**
   * Checks if a rank is greater or equal than another via inheritance
   *
   * @param testRank rank to test against
   * @param rank provided rank
   */
  public static boolean isGreaterThan(Rank testRank, Rank rank) {
    if (testRank.name.equalsIgnoreCase(rank.name)) {
      return true;
    }
    for (String inh : testRank.inheritance) {
      if (inh.equalsIgnoreCase(rank.name)) {
        return true;
      }
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
      if (test == null) {
        ServerEssentials.LOG.warn(
            "Failed to get rank '" + ((test == null) ? testRank : rank) + "'");
      }
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
    for (String rank : ranks) {
      if (isGreaterThan(testRank, rank)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasPermission(Account account, String perm) {
    if (perm.equals("*")) { // Assuming Trusted Required
      return TrustedList.trustedUsers.contains(account.uuid);
    }
    // Perm Indexing
    String core = "";
    String base = "";
    String sub = "";
    if (perm.contains(".")) {
      core = perm.substring(0, perm.indexOf("."));
      base = perm.substring(perm.indexOf("."));
      if (base.contains(".")) {
        sub = base.substring(base.indexOf("."));
        base = base.substring(0, base.indexOf("."));
      }
    } else {
      core = perm;
    }

    for (String p : account.perms) {
      if (perm.equals(p) || p.equals("*")) {
        return true;
      }

      // Perm Indexing
      String pCore = "";
      String pBase = "";
      String pSub = "";
      if (p.contains(".")) {
        pCore = p.substring(0, p.indexOf("."));
        pBase = p.substring(p.indexOf("."));
        if (pBase.contains(".")) {
          pSub = pBase.substring(pBase.indexOf("."));
          pBase = pBase.substring(0, pBase.indexOf("."));
        }
      } else {
        pCore = p;
      }

      // Check for matches
      // Exact
      if (core.equalsIgnoreCase(pCore) && base.equalsIgnoreCase(pBase)
          && sub.equalsIgnoreCase(pSub)) {
        return true;
      }
      // Wildcard match
      if (core.equalsIgnoreCase(pCore) && base.equalsIgnoreCase(pBase) && (
          sub.isEmpty() && pSub.isEmpty() || pSub.equalsIgnoreCase("*"))) {
        return true;
      }
    }
    return false;
  }
}
