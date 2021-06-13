package com.wurmcraft.serveressentials.api.models.config;

public class ConfigGlobal {

    public General general;
    public String[] enabledModules;

    public ConfigGlobal() {
        this.general = new General();
        this.enabledModules = new String[] {"General"};
    }

    public ConfigGlobal(General general) {
        this.general = general;
    }


    public static class General {

        public boolean debug;

        public General(boolean debug) {
            this.debug = debug;
        }

        public General() {
            this.debug = false;
        }
    }
}
