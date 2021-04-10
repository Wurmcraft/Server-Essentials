package io.wurmatron.serveressentials.config;

public class DatabaseConfig  {

    public String username;
    public String password;
    public int port;
    public String host;
    public String database;
    public String sqlParams;

    public DatabaseConfig(String username, String password, int port, String host, String database, String sqlParams) {
        this.username = username;
        this.password = password;
        this.port = port;
        this.host = host;
        this.database = database;
        this.sqlParams = sqlParams;
    }

    public DatabaseConfig() {
        this.username = "se-modded";
        this.password = "";
        this.port = 3306;
        this.host = "localhost";
        this.database = "server-essentials";
        this.sqlParams = "useSSL=false";
    }
}
