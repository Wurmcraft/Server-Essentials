/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.discord;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.data_wrapper.ChatMessage;
import java.util.Map;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class DiscordBot {

  public static GatewayDiscordClient client;
  public static NonBlockingHashMap<String, Map<String, String>> channelMap;

  public static void start() {
    LOG.info("Discord Bot is starting");
    client =
        DiscordClientBuilder.create(ServerEssentialsRest.config.discord.token)
            .build()
            .login()
            .block();
    addEvents();
    createChannelMap();
    client.onDisconnect().block();
  }

  private static void addEvents() {
    // Confirm Login Event
    client
        .getEventDispatcher()
        .on(ReadyEvent.class)
        .subscribe(
            event -> {
              User self = event.getSelf();
              LOG.info(
                  String.format(
                      "Bot Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
            });
  }

  private static void createChannelMap() {
    // Create Channel Lookup table
    channelMap = new NonBlockingHashMap<>();
    for (String serverID : ServerEssentialsRest.config.discord.channelMap.keySet()) {
      String[] split = ServerEssentialsRest.config.discord.channelMap.get(serverID).split(";");
      NonBlockingHashMap<String, String> serverChannelMap = new NonBlockingHashMap<>();
      for (String s : split) {
        String[] channelData = s.split(":");
        if (channelData.length == 2) serverChannelMap.put(channelData[0], channelData[1]);
        else
          LOG.warn(
              "Invalid Channel map '"
                  + serverID
                  + "' must be in the format channelName:discordChannelID not '"
                  + String.join(":", channelData)
                  + "'");
      }
      channelMap.put(serverID, serverChannelMap);
    }
  }

  public static void sendMessage(ChatMessage message) {
    if (channelMap == null) createChannelMap();
    // Check if the message can be mapped
    if (channelMap.containsKey(message.serverID)
        && channelMap.get(message.serverID).containsKey(message.channel)) {
      String channelID = channelMap.get(message.serverID).get(message.channel);
      client.rest().getChannelById(Snowflake.of(channelID)).createMessage(message.message).block();
    }
  }
}
