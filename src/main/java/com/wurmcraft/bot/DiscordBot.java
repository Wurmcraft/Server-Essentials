package com.wurmcraft.bot;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.bot.json.api.Player;
import com.wurmcraft.bot.json.api.Players;
import com.wurmcraft.bot.json.api.data.Token;
import com.wurmcraft.bot.json.api.json.CommandQueue.RequestedCommand;
import com.wurmcraft.bot.json.api.status.TrackingStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;


public class DiscordBot {

  public static BotConfig config = null;

  // Config values
  private static int keyLength;
  private static String serverID;
  private static long verifyRole;
  public static String auth;
  public static String baseURL;

  public static HashMap<Long, String> verifiedUsers = new HashMap<>();
  private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static Set<Long> deleteQueue = new HashSet<>();
  public static Set<Long> sendToSpawnQueue = new HashSet<>();
  public static Set<Long> kickQueue = new HashSet<>();

  public static void main(String[] args) throws IOException {
    File botConfig = new File("config.json");
    if (botConfig.exists()) {
      config = GSON.fromJson(String.join("\n", Files.readAllLines(botConfig.toPath())),
          BotConfig.class);
      keyLength = config.codeLength;
      serverID = config.discordServerID;
      verifyRole = config.verifiedRole;
      auth = config.restAuth;
      baseURL = config.restURL;
    } else {
      Files.write(botConfig.toPath(), GSON.toJson(new BotConfig()).getBytes(),
          StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
      System.out.println("Bot needs configuration, before it can start!");
    }
    if (config != null) {
      DiscordApi api = (new DiscordApiBuilder()).setToken(config.discordBotToken).login()
          .join();
      FallbackLoggerConfiguration.setDebug(true);
      api.addMessageCreateListener(e -> {
        boolean valid = false;
        // Prevent the same discord ID from being verified multiple times
        if (e.isPrivateMessage() && e.getMessage().getContent()
            .equalsIgnoreCase("!verify")
            && verifiedUsers.containsKey(e.getMessage().getUserAuthor().get().getId())) {
          e.getChannel().sendMessage("You have already been verified!");
        } else {
          if (e.isPrivateMessage() && e.getMessage().getContent()
              .equalsIgnoreCase("!verify")) {
            String key = generateKey();
            e.getChannel().sendMessage("Your code is '" + key + "'");
            System.out.println("HTTP Code: " + RequestGenerator.INSTANCE
                .post("api/discord/add",
                    new Token("" + e.getMessageAuthor().getId(), key)));
            e.getChannel().sendMessage(config.verifyCodeMessage);
          }
        }
        if (e.isPrivateMessage() && e.getMessage().getContent().equalsIgnoreCase("!help")
            || e.isPrivateMessage() && e.getMessage().getContent()
            .equalsIgnoreCase("help")) {
          e.getMessage().getChannel()
              .sendMessage(
                  "!verify (Gives a code for use in-game to verify your discord account)");
          e.getMessage().getChannel()
              .sendMessage(
                  "!deletePlayerFile (Deletes your playerfile on a given server)");
          e.getMessage().getChannel()
              .sendMessage(
                  "!sendToSpawn (Teleport's a player to spawn)");
          e.getMessage().getChannel()
              .sendMessage(
                  "!kick (Kicks you from the given server)");
        }
        if (e.isPrivateMessage() && e.getMessage().getContent()
            .equalsIgnoreCase("!deletePlayerFile")) {
          if (verifiedUsers.containsKey(e.getMessage().getUserAuthor().get().getId())) {
            e.getMessage().getChannel()
                .sendMessage(
                    "Please specific a serverID from the following: ");
            deleteQueue.add(e.getMessage().getUserAuthor().get().getId());
            for (TrackingStatus status : RequestGenerator.Track.getStatus()) {
              e.getMessage().getChannel().sendMessage(status.serverID);
            }
            valid = true;
          } else {
            e.getMessage().getChannel()
                .sendMessage(
                    "You must be verified to do that!");
          }
        }
        if (e.isPrivateMessage() && e.getMessage().getContent()
            .equalsIgnoreCase("!sendToSpawn")) {
          if (verifiedUsers.containsKey(e.getMessage().getUserAuthor().get().getId())) {
            e.getMessage().getChannel()
                .sendMessage(
                    "Please specific a serverID from the following: ");
            sendToSpawnQueue.add(e.getMessage().getUserAuthor().get().getId());
            TrackingStatus[] statuss = RequestGenerator.Track.getStatus();
            if (statuss.length > 0) {
              for (TrackingStatus status : statuss) {
                e.getMessage().getChannel().sendMessage(status.serverID);
              }
            } else {
              e.getMessage().getChannel()
                  .sendMessage(
                      "No Server's currently have the Status module enabled, Contact a server admin!");
            }
            valid = true;

          } else {
            e.getMessage().getChannel()
                .sendMessage(
                    "You must be verified to do that!");
          }
        }
        if (e.isPrivateMessage() && e.getMessage().getContent()
            .equalsIgnoreCase("!kick")) {
          if (verifiedUsers.containsKey(e.getMessage().getUserAuthor().get().getId())) {
            e.getMessage().getChannel()
                .sendMessage(
                    "Please specific a serverID from the following: ");
            kickQueue.add(e.getMessage().getUserAuthor().get().getId());
            TrackingStatus[] statuss = RequestGenerator.Track.getStatus();
            if (statuss.length > 0) {
              for (TrackingStatus status : statuss) {
                e.getMessage().getChannel().sendMessage(status.serverID);
              }
            } else {
              e.getMessage().getChannel()
                  .sendMessage(
                      "No Server's currently have the Status module enabled, Contact a server admin!");
            }
            valid = true;

          } else {
            e.getMessage().getChannel()
                .sendMessage(
                    "You must be verified to do that!");
          }
        }
        if (e.isPrivateMessage() && deleteQueue
            .contains(e.getMessage().getUserAuthor().get().getId())
            || e.isPrivateMessage() && sendToSpawnQueue
            .contains(e.getMessage().getUserAuthor().get().getId()) || e.isPrivateMessage() && kickQueue
            .contains(e.getMessage().getUserAuthor().get().getId())) {
          String id = e.getMessage().getContent();
          for (TrackingStatus status : RequestGenerator.Track.getStatus()) {
            if (id.equalsIgnoreCase(status.serverID)) {
              if (deleteQueue.contains(e.getMessage().getUserAuthor().get().getId())) {
                e.getMessage().getChannel()
                    .sendMessage(
                        "The Delete Player File request has been sent, Please allow up to 90 seconds for it to complete!");
                RequestGenerator.Commands.newCommand(new RequestedCommand(status.serverID,
                    "dpf " + verifiedUsers
                        .get(e.getMessage().getUserAuthor().get().getId()), ""));
              } else if (sendToSpawnQueue
                  .contains(e.getMessage().getUserAuthor().get().getId())) {
                e.getMessage().getChannel()
                    .sendMessage(
                        "You have been sent to spawn");
                RequestGenerator.Commands.newCommand(new RequestedCommand(status.serverID,
                    "sendtospawn " + verifiedUsers
                        .get(e.getMessage().getUserAuthor().get().getId()), ""));
              } else if (kickQueue
                  .contains(e.getMessage().getUserAuthor().get().getId())) {
                e.getMessage().getChannel()
                    .sendMessage(
                        "You you have been queued to be kicked");
                RequestGenerator.Commands.newCommand(new RequestedCommand(status.serverID,
                    "kick " + verifiedUsers
                        .get(e.getMessage().getUserAuthor().get().getId()), verifiedUsers
                    .get(e.getMessage().getUserAuthor().get().getId())));
              }
              break;
            }
          }
        }
        if (e.isPrivateMessage() && !valid) {
          deleteQueue.remove(e.getMessage().getUserAuthor().get().getId());
          sendToSpawnQueue.remove(e.getMessage().getUserAuthor().get().getId());
          kickQueue.remove(e.getMessage().getUserAuthor().get().getId());
        }
      });
      api.getThreadPool().getScheduler().scheduleAtFixedRate(() -> {
        System.out.println("Checking for new user's to verify!");
        Player[] players = (RequestGenerator.INSTANCE
            .get("/user", Players.class)).players;
        verifiedUsers.clear();
        for (Player player : players) {
          if (player.discordID != null && !player.discordID.isEmpty()) {
            try {
              User user = api.getUserById(player.discordID).get();
              List<Role> roles = user.getRoles(api.getServerById(serverID).get());
              boolean verify = false;
              for (Role role : roles) {
                if (role.getId() == verifyRole && role.getUsers().contains(user)) {
                  verify = true;
                }
              }
              if (!verify) {
                System.out.println(
                    "User '" + user.getName() + "' has been verified with '" + player.uuid
                        + "'");
                if (api.getRoleById(verifyRole).isPresent()) {
                  Role verifyDiscord = api.getRoleById(verifyRole).get();
                  verifyDiscord.addUser(user);
                  user.sendMessage(config.userVerified);
                }
              }
              verifiedUsers.put(user.getId(), player.uuid);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      }, 0L, 20, TimeUnit.MINUTES);
    }
  }

  private static String generateKey() {
    int lower = 48;
    int higher = 122;
    Random random = new Random();
    return (random.ints(lower, higher + 1)
        .filter(i -> ((i <= 57 || i >= 65) && (i <= 90 || i >= 97))).limit(keyLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint,
            StringBuilder::append)).toString();
  }
}
