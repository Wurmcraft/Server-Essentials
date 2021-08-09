package io.wurmatron.serveressentials.config;

public class DiscordConfig {

    public String token;

    public DiscordConfig(String token) {
        this.token = token;
    }

    public DiscordConfig() {
        this.token = "";
    }
}
