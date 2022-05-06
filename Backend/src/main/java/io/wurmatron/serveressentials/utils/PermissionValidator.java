/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.utils;

public class PermissionValidator {

  public static final String WILDCARD = "*";

  /**
   * Check if 2 permission nodes are the same
   *
   * @param actual permission for the requested endpoint / command
   * @param test permission to check if it matches
   * @return if the permissions match
   */
  public static boolean isSamePerm(String actual, String test) {
    if (test.contains(WILDCARD)) {
      return validateWildcard(actual, test);
    } else { // No Wildcard, Direct check
      long actualLength = actual.chars().filter(ch -> ch == '.').count();
      long testLength = test.chars().filter(ch -> ch == '.').count();
      if (actualLength == testLength) {
        for (int x = 0; x < actualLength; x++) {
          String actualPoint = getPermPoint(actual, x);
          String testPoint = getPermPoint(test, x);
          if (!actualPoint.equalsIgnoreCase(testPoint)) return false;
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the provided perms are equal, with wildcard
   *
   * @param actual permission for the requested endpoint / command
   * @param test permission to check if it matches
   * @return if the permissions match
   */
  private static boolean validateWildcard(String actual, String test) {
    if (test.contains(WILDCARD)) {
      int wildcardIndex = wildcardIndex(test);
      if (wildcardIndex == 0) { // *.X.X.X
        return true;
      }
      // Check up until the wildcard
      for (int x = 0; x < wildcardIndex; x++) {
        String actualPoint = getPermPoint(actual, x);
        String testPoint = getPermPoint(test, x);
        if (!actualPoint.equalsIgnoreCase(testPoint)) return false;
      }
      return true;
    }
    return false;
  }

  /**
   * Find the locational index of the wildcard within the string / permission
   *
   * @param perm permission to find the wildcard from
   * @return index of the wildcard
   */
  private static int wildcardIndex(String perm) {
    long count = perm.chars().filter(ch -> ch == '.').count();
    String[] split = perm.split("\\.");
    for (int x = 0; x < count; x++) if (split[x].equalsIgnoreCase(WILDCARD)) return x;
    return -1;
  }

  /**
   * Pulls out the section of the perm node based on the 'point' / index Example:
   * module.command.type.sub a 2 would return 'type', while a 1 would return 'command'
   *
   * @param perm command perm to check
   * @param point index of the node within the command perm
   * @return section of the perm at the provided index
   */
  private static String getPermPoint(String perm, int point) {
    long count = perm.chars().filter(ch -> ch == '.').count();
    if (count > point) {
      String[] split = perm.split("\\.");
      return split[point].trim();
    }
    // Non existent
    return "";
  }
}
