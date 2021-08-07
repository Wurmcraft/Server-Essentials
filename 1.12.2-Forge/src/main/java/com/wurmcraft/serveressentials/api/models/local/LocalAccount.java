package com.wurmcraft.serveressentials.api.models.local;

public class LocalAccount {

    public String uuid;
    public String channel;
    public String[] ignoredUsers;

    public LocalAccount(String uuid) {
        this.uuid = uuid;
        this.ignoredUsers = new String[0];
    }

    public LocalAccount(String uuid, String channel, String[] ignoredUsers) {
        this.uuid = uuid;
        this.channel = channel;
        this.ignoredUsers = ignoredUsers;
    }

    public LocalAccount() {
    }
}
