package io.wurmatron.serveressentials.config;

public class DatabaseConfig  {

    public String username;
    public String password;
    public int port;
    public String host;

    public DatabaseConfig(String username, String password, int port, String host) {
        this.username = username;
        this.password = password;
        this.port = port;
        this.host = host;
    }

    public DatabaseConfig() {
        this.username = "se-modded";
        this.password = "";
        this.port = 1433;
        this.host = "localhost";
    }
}
