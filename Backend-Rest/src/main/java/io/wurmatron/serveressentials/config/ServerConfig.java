package io.wurmatron.serveressentials.config;

public class ServerConfig {

    public int port;
    public String host;
    public String corosOrigins;
    public long requestTimeout;
    public boolean forceLowercase;
    public boolean swaggerEnabled;

    public ServerConfig(int port, String host, String corosOrigins, long requestTimeout, boolean forceLowercase, boolean swaggerEnabled) {
        this.port = port;
        this.host = host;
        this.corosOrigins = corosOrigins;
        this.requestTimeout = requestTimeout;
        this.forceLowercase = forceLowercase;
        this.swaggerEnabled = swaggerEnabled;
    }

    public ServerConfig() {
        this.port = 8080;
        this.host = "";
        this.corosOrigins = "";
        this.requestTimeout = 5000;
        this.forceLowercase = true;
        this.swaggerEnabled = false;
    }
}
