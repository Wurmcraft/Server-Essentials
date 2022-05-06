package io.wurmatron.serveressentials.models;

public class ServerAuth {

    public String server_id;
    public String token;
    public String key;
    public String ip;

    public ServerAuth(String serverID, String token, String key, String ip) {
        this.server_id = serverID;
        this.token = token;
        this.key = key;
        this.ip = ip;
    }
}
