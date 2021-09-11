package com.wurmcraft.serveressentials.api.models.local;

import java.util.HashMap;

public class LocalAccount {

    public String uuid;
    public String channel;
    public String[] ignoredUsers;
    public boolean socialSpy;
    public long teleportTimer;
    public Location lastLocation;
    public Home[] homes;
    public HashMap<String, Long> kitUsage;

    public LocalAccount(String uuid) {
        this.uuid = uuid;
        this.ignoredUsers = new String[0];
        this.socialSpy = false;
        this.homes = new Home[0];
        kitUsage = new HashMap<>();
    }

    public LocalAccount(String uuid, String channel, String[] ignoredUsers, boolean socialSpy, long teleportTimer, Location lastLocation, Home[] homes, HashMap<String, Long> kitUsage) {
        this.uuid = uuid;
        this.channel = channel;
        this.ignoredUsers = ignoredUsers;
        this.socialSpy = socialSpy;
        this.teleportTimer = teleportTimer;
        this.lastLocation = lastLocation;
        this.homes = homes;
        this.kitUsage = kitUsage;
    }

    public LocalAccount() {
    }
}
