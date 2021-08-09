package io.wurmatron.serveressentials.discord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import io.wurmatron.serveressentials.ServerEssentialsRest;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class DiscordBot {

    public static GatewayDiscordClient client;

    public static void start() {
        LOG.info("Discord Bot is starting");
        client = DiscordClientBuilder.create(ServerEssentialsRest.config.discord.token).build().login().block();
        addEvents();
        client.onDisconnect().block();
    }

    private static void addEvents() {
        // Confirm Login Event
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    LOG.info(String.format("Bot Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });
    }
}
