package com.wurmcraft.serveressentials.api.models.config;

public class ConfigGlobal {

    public General general;
    public Storage storage;
    public Performance performance;
    public String[] enabledModules;

    public ConfigGlobal() {
        this.general = new General();
        this.storage = new Storage();
        this.performance = new Performance();
        this.enabledModules = new String[]{"General"};
    }

    public ConfigGlobal(General general) {
        this.general = general;
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
    }

    public static class Performance {

        public int maxThreads;
        public int playerCacheTimeout;

        public Performance(int maxThreads, int playerCacheTimeout) {
            this.maxThreads = maxThreads;
            this.playerCacheTimeout = playerCacheTimeout;
        }

        public Performance() {
            this.maxThreads = 4;
            this.playerCacheTimeout = 300;
        }
    }
}
