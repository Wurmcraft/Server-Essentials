package com.wurmcraft.serveressentials.forge.api.json;

import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import java.util.HashMap;
import java.util.Map;

public class SpawnLocation {

  public Map<String, LocationWrapper> spawns;

  public SpawnLocation() {
    this.spawns = new HashMap<>();
  }

  public SpawnLocation(
      Map<String, LocationWrapper> spawns) {
    this.spawns = spawns;
  }
}
