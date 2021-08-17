package com.wurmcraft.serveressentials.api.models.local;

public class LocalAccount {

    public String uuid;
    public String channel;
    public String[] ignoredUsers;
    public boolean socialSpy;
    public long teleportTimer;
    public Location lastLocation;
    public Home[] homes;

    public LocalAccount(String uuid) {
        this.uuid = uuid;
        this.ignoredUsers = new String[0];
        this.socialSpy = false;
        this.homes = new Home[0];
    }

    public LocalAccount(String uuid, String channel, String[] ignoredUsers, boolean socialSpy, long teleportTimer, Location lastLocation, Home[] homes) {
        this.uuid = uuid;
        this.channel = channel;
        this.ignoredUsers = ignoredUsers;
        this.socialSpy = socialSpy;
        this.teleportTimer = teleportTimer;
        this.lastLocation = lastLocation;
        this.homes = homes;
    }

    public LocalAccount() {
    }
}
