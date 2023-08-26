package com.wurmcraft.serveressentials.common.modules.security;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.common.modules.security.event.SecurityEvents;
import com.wurmcraft.serveressentials.common.utils.URLUtils;
import java.util.ArrayList;
import java.util.List;

public class TrustedList {

  public static List<String> trustedUsers = new ArrayList<>();

  public static void load() {
    try {
      String url = URLUtils.get(SecurityEvents.config.trustedList);
      for (String line : url.split("\n")) {
        trustedUsers.add(line.trim());
      }
    } catch (Exception e) {
      ServerEssentials.LOG.warn(
          "Unable to load trusted users from '" + SecurityEvents.config.trustedList
              + "' verify the url exists and is in the correct format.");
    }
    if (trustedUsers.size() == 0) {
      ServerEssentials.LOG.warn("Trusted User's list is empty");
    }
  }
}
