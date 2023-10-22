package com.wurmcraft.serveressentials.api.models.config;

import java.util.Arrays;
import java.util.Objects;

public class ConfigGlobal {

  public General general;
  public Storage storage;
  public Performance performance;
  public String[] enabledModules;
  public String configVersion;

  public ConfigGlobal() {
    this.general = new General();
    this.storage = new Storage();
    this.performance = new Performance();
    this.enabledModules = new String[] {"General", "AutoRank", "Chat", "Economy", "Rank"};
    this.configVersion = Integer.toHexString(hashCode());
  }

  public ConfigGlobal(
      General general,
      Storage storage,
      Performance performance,
      String[] enabledModules,
      String configVersion) {
    this.general = general;
    this.storage = storage;
    this.performance = performance;
    this.enabledModules = enabledModules;
    this.configVersion = configVersion;
  }

  public static class General {

    public boolean debug;
    public String serverID;

    public General(boolean debug, String serverID) {
      this.debug = debug;
      this.serverID = serverID;
    }

    public General() {
      this.debug = false;
      this.serverID = "Not-Set";
    }

    @Override
    public int hashCode() {
      return Objects.hash(debug, serverID);
    }
  }

  public static class Storage {

    public String storageType;
    public String token;
    public String key;
    public String baseURL;

    public Storage(String storageType, String token, String key, String baseURL) {
      this.storageType = storageType;
      this.token = token;
      this.key = key;
      this.baseURL = baseURL;
    }

    public Storage() {
      this.storageType = "File";
      this.token = "";
      this.key = "";
      this.baseURL = "https://localhost:8080/";
    }

    @Override
    public int hashCode() {
      return Objects.hash(storageType, token, key, baseURL);
    }
  }

  public static class Performance {

    public int maxThreads;
    public int playerCacheTimeout;
    public int playerSyncInterval;
    public boolean useWebsocket;
    public int dataloaderInterval;

    public Performance(
        int maxThreads,
        int playerCacheTimeout,
        int playerSyncInterval,
        boolean useWebsocket,
        int dataloaderInterval) {
      this.maxThreads = maxThreads;
      this.playerCacheTimeout = playerCacheTimeout;
      this.playerSyncInterval = playerSyncInterval;
      this.useWebsocket = useWebsocket;
      this.dataloaderInterval = dataloaderInterval;
    }

    public Performance() {
      this.maxThreads = 4;
      this.playerCacheTimeout = 300;
      this.playerSyncInterval = 90;
      this.useWebsocket = false;
      this.dataloaderInterval = 5;
    }

    @Override
    public int hashCode() {
      return Objects.hash(maxThreads, playerCacheTimeout, playerSyncInterval, useWebsocket);
    }
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(general, storage, performance, configVersion);
    result = 31 * result + Arrays.hashCode(enabledModules);
    return result;
  }
}
