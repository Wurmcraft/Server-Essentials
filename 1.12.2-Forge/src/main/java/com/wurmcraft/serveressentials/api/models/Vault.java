package com.wurmcraft.serveressentials.api.models;

public class Vault {

    public String ownerUUID;
    public String name;
    public String[] items;
    public int maxPages;

    public Vault(String ownerUUID, String name, String[] items, int maxPages) {
        this.ownerUUID = ownerUUID;
        this.name = name;
        this.items = items;
        this.maxPages = maxPages;
    }

    public Vault(String ownerUUID, String name, int maxPages) {
        this.ownerUUID = ownerUUID;
        this.name = name;
        this.items = new String[maxPages * 45];
        this.maxPages = maxPages;
    }
}
