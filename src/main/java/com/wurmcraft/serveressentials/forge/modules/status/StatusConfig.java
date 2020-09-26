package com.wurmcraft.serveressentials.forge.modules.status;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Status")
public class StatusConfig implements JsonParser {

  public int updatePeriod;
  public String updateType;

  public StatusConfig() {
    this.updatePeriod = 90;
    this.updateType = "Standard";
  }

  public StatusConfig(int updatePeriod, String updateType) {
    this.updatePeriod = updatePeriod;
    this.updateType = updateType;
  }

  @Override
  public String getID() {
    return "Status";
  }
}
