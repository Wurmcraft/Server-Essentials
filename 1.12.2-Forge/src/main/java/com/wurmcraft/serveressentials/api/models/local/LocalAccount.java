package com.wurmcraft.serveressentials.api.models.local;

public class LocalAccount {

    public String uuid;
    public String channel;

    public LocalAccount(String uuid) {
        this.uuid = uuid;
    }

    public LocalAccount(String uuid, String channel) {
        this.uuid = uuid;
        this.channel = channel;
    }

    public LocalAccount() {
    }
}
