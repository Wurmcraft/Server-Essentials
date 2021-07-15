package com.wurmcraft.serveressentials.api.models;

import java.util.Map;

public class Channel {

    public String name;
    public String prefix;
    public boolean logChat;
    public Map<String, String> chatReplacment;

    public Channel() {
    }

    public Channel(String name, String prefix, boolean logChat, Map<String, String> chatFilter) {
        this.name = name;
        this.prefix = prefix;
        this.logChat = logChat;
        this.chatReplacment = chatFilter;
    }
}
