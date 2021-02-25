package com.wurmcraft.server_essentials.rest.config;

public class RestConfig implements Config{

    public String host;
    public int port;
    public Database database;

    public RestConfig() {
        this.host = "localhost";
        this.port = 8760;
        this.database = new Database("localhost","3306","serveressentials","","server-essentials");
    }

    public RestConfig(String host, int port, Database database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    public class Database {
        public String address;
        public String port;
        public String username;
        public String password;
        public String database_name;

        public Database(String address, String port, String username, String password, String database_name) {
            this.address = address;
            this.port = port;
            this.username = username;
            this.password = password;
            this.database_name = database_name;
        }
    }

    @Override
    public String getFileName() {
        return "config.json";
    }
}
