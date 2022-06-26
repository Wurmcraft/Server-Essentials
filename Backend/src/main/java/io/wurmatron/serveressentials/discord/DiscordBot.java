/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.discord;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.UserGuildData;
import discord4j.rest.entity.RestRole;
import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.cli.CommandParser;
import io.wurmatron.serveressentials.models.DiscordVerify;
import io.wurmatron.serveressentials.models.data_wrapper.ChatMessage;
import java.util.Map;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import reactor.core.publisher.Flux;

public class DiscordBot {

  public static GatewayDiscordClient client;
  public static NonBlockingHashMap<String, Map<String, String>> channelMap;
  public static Snowflake guildID;
  public static Snowflake verifiedRank;

  public static void start() {
    LOG.info("Discord Bot is starting");
    client =
        DiscordClientBuilder.create(ServerEssentialsRest.config.discord.token)
            .build()
            .login()
            .block();
    addEvents();
    setupVariables();
    createChannelMap();
    ApplicationCommandRequest verifyCommand = ApplicationCommandRequest.builder()
        .name("verify")
        .description("Generates a code to verify in-game")
        .addOption(ApplicationCommandOptionData.builder()
            .name("username")
            .description("Your in-Game username")
            .type(ApplicationCommandOption.Type.STRING.getValue())
            .required(true)
            .build()
        ).build();
    long applicationId = client.getRestClient().getApplicationId().block();
    client.getRestClient().getApplicationService()
        .createGlobalApplicationCommand(applicationId, verifyCommand).subscribe();
    client.onDisconnect().block();
  }

  private static void setupVariables() {
    try {
      Flux<UserGuildData> guilds = client.getRestClient().getGuilds();
      guildID = Snowflake.of(guilds.blockFirst().id());
      try {
        RestRole role = client.getRestClient().getRoleById(guildID,
            Snowflake.of(ServerEssentialsRest.config.discord.verifiedRankID));
        verifiedRank = role.getId();
      } catch (Exception e) {
        LOG.warn(
            "Unable to find role '" + ServerEssentialsRest.config.discord.verifiedRankID
                + "'");
      }
    } catch (Exception e) {
      LOG.warn("Bot is not connected to any servers!, Shutting Down.");
      CommandParser.handle("stop");
    }
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
                      "Bot Logged in as %s#%s", self.getUsername(),
                      self.getDiscriminator()));
            });
    client.getEventDispatcher().on(ChatInputInteractionEvent.class, event -> {
      if (event.getCommandName().equals("verify")) {
        return BotCommands.verify(event);
      }
      return null;
    }).subscribe();
  }

  private static void createChannelMap() {
    // Create Channel Lookup table
    channelMap = new NonBlockingHashMap<>();
    for (String serverID : ServerEssentialsRest.config.discord.channelMap.keySet()) {
      String[] split = ServerEssentialsRest.config.discord.channelMap.get(serverID)
          .split(";");
      NonBlockingHashMap<String, String> serverChannelMap = new NonBlockingHashMap<>();
      for (String s : split) {
        String[] channelData = s.split(":");
        if (channelData.length == 2) {
          serverChannelMap.put(channelData[0], channelData[1]);
        } else {
          LOG.warn(
              "Invalid Channel map '"
                  + serverID
                  + "' must be in the format channelName:discordChannelID not '"
                  + String.join(":", channelData)
                  + "'");
        }
      }
      channelMap.put(serverID, serverChannelMap);
    }
  }

  public static void sendMessage(ChatMessage message) {
    if (channelMap == null) {
      createChannelMap();
    }
    // Check if the message can be mapped
    if (channelMap.containsKey(message.serverID)
        && channelMap.get(message.serverID).containsKey(message.channel)) {
      String channelID = channelMap.get(message.serverID).get(message.channel);
      client.rest().getChannelById(Snowflake.of(channelID)).createMessage(message.message)
          .block();
    }
  }

  public static void verifyUser(DiscordVerify verify) {
    if (verifiedRank != null) {
      Member member = client.getMemberById(guildID, Snowflake.of(verify.discordID))
          .block();
      member.addRole(verifiedRank).block();
      member.getPrivateChannel().block().createMessage("You have been verified!")
          .block(); // TODO Lang support
    }
  }
}
