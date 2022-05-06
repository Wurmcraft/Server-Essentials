/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.config;

import java.util.HashMap;
import java.util.Map;

public class DiscordConfig {

  public String token;
  public Map<String, String> channelMap;

  public DiscordConfig(String token, Map<String, Object> map) {
    this.token = token;
    this.channelMap = new HashMap<>();
    for (String m : map.keySet()) channelMap.put(m, (String) map.get(m));
  }

  public DiscordConfig() {
    this.token = "";
    this.channelMap = new HashMap<>();
    channelMap.put("test", "local:759122277525749770");
  }
}
