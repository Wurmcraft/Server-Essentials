package io.wurmatron.serveressentials.routes.data;

public class ServerAuth {

    public String serverID;
    public String token;
    public String key;
    public String ip;

    public ServerAuth(String serverID, String token, String key, String ip) {
        this.serverID = serverID;
        this.token = token;
        this.key = key;
        this.ip = ip;
    }
}
