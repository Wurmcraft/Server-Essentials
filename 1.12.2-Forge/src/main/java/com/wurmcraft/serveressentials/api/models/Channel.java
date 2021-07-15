package com.wurmcraft.serveressentials.api.models;

public class Channel {

    public String name;
    public String prefix;

    public Channel() {
    }

    public Channel(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }
}
