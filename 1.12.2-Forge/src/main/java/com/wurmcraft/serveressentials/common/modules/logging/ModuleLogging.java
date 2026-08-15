package com.wurmcraft.serveressentials.common.modules.logging;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;

import java.util.ArrayList;
import java.util.List;

@Module(name = "Logging")
public class ModuleLogging {

  public void setup() {}

  public void reload() {}

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigLogging cfg = (ConfigLogging) SECore.moduleConfigs.get("LOGGING");
    info.add("loggingLocation: " +  cfg.loggingLocation);
    info.add("logging>attackBlock: " +  cfg.logging.attackBlock);
    info.add("logging>attackEntity: " +  cfg.logging.attackEntity);
    info.add("logging>blockBreak: " +  cfg.logging.blockBreak);
    info.add("logging>blockPlace: " +  cfg.logging.blockPlace);
    info.add("logging>explosion: " +  cfg.logging.explosion);
    info.add("logging>interactAir: " +  cfg.logging.interactAir);
    info.add("logging>interactLeft: " +  cfg.logging.interactLeft);
    info.add("logging>interactRight: " +  cfg.logging.interactRight);
    info.add("database>host: " +  cfg.database.host);
    info.add("database>password: " +  ((cfg.database.password).isEmpty() ? "Empty" : "********"));
    info.add("database>sqlParams: " +  cfg.database.sqlParams);
    info.add("database>username: " +  ((cfg.database.username).isEmpty() ? "Empty" : "********"));
    return info.toArray(new String[0]);
  }
}
