package com.wurmcraft.serveressentials.api.models.local;

public class LocalAccount {

    public String uuid;
    public String channel;
    public String[] ignoredUsers;
    public boolean socialSpy;

    public LocalAccount(String uuid) {
        this.uuid = uuid;
        this.ignoredUsers = new String[0];
        this.socialSpy = false;
    }

    public LocalAccount(String uuid, String channel, String[] ignoredUsers, boolean socialSpy) {
        this.uuid = uuid;
        this.channel = channel;
        this.ignoredUsers = ignoredUsers;
        this.socialSpy = socialSpy;
    }

    public LocalAccount() {
    }
}
