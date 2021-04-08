package io.wurmatron.serveressentials.config;

public class ServerConfig {

    public int port;

    public ServerConfig(int port) {
        this.port = port;
    }

    public ServerConfig() {
        this.port = 8080;
    }
}
