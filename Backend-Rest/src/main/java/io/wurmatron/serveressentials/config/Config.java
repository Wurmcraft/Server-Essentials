package io.wurmatron.serveressentials.config;

public class Config {

    public GeneralConfig general;
    public DatabaseConfig database;
    public ServerConfig server;

    public Config(GeneralConfig general, DatabaseConfig database, ServerConfig server) {
        this.general = general;
        this.database = database;
        this.server = server;
    }

    public Config() {
        this.general = new GeneralConfig();
        this.database = new DatabaseConfig();
        this.server = new ServerConfig();
    }
}
