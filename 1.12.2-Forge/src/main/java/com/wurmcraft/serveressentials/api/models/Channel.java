package com.wurmcraft.serveressentials.api.models;

import java.util.Map;

public class Channel {

    public String name;
    public String prefix;
    public boolean logChat;
    public Map<String, String> chatReplacment;
    public boolean enabled;

    public Channel() {
    }

    public Channel(String name, String prefix, boolean logChat, Map<String, String> chatReplacment, boolean enabled) {
        this.name = name;
        this.prefix = prefix;
        this.logChat = logChat;
        this.chatReplacment = chatReplacment;
        this.enabled = enabled;
    }
}
