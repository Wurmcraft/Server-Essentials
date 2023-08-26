package com.wurmcraft.serveressentials.common.modules.logging;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Logging")
public class ConfigLogging {

  public String loggingLocation;
  public SQL database;
  public Logging logging;

  public ConfigLogging(String loggingLocation, SQL database, Logging logging) {
    this.loggingLocation = loggingLocation;
    this.database = database;
    this.logging = logging;
  }

  public ConfigLogging() {
    this.loggingLocation = "Rest";
    this.database = new SQL();
    this.logging = new Logging();
  }

  public static class SQL {

    public String username;
    public String password;
    public String host;
    public String sqlParams;

    public SQL(String username, String password, String host, String sqlParams) {
      this.username = username;
      this.password = password;
      this.host = host;
      this.sqlParams = sqlParams;
    }

    public SQL() {
      this.username = "server-essentials";
      this.password = "";
      this.host = "localhost";
      this.sqlParams = "";
    }
  }

  public static class Logging {

    public boolean blockBreak;
    public boolean blockPlace;
    public boolean interactAir;
    public boolean interactLeft;
    public boolean interactRight;
    public boolean attackBlock;
    public boolean attackEntity;
    public boolean explosion;

    public Logging(
        boolean blockBreak,
        boolean blockPlace,
        boolean interactAir,
        boolean interactLeft,
        boolean interactRight,
        boolean attackBlock,
        boolean attackEntity,
        boolean explosion) {
      this.blockBreak = blockBreak;
      this.blockPlace = blockPlace;
      this.interactAir = interactAir;
      this.interactLeft = interactLeft;
      this.interactRight = interactRight;
      this.attackBlock = attackBlock;
      this.attackEntity = attackEntity;
      this.explosion = explosion;
    }

    public Logging() {
      this.blockBreak = true;
      this.blockPlace = true;
      this.interactAir = true;
      this.interactLeft = true;
      this.interactRight = true;
      this.attackBlock = true;
      this.attackEntity = true;
      this.explosion = true;
    }
  }
}
