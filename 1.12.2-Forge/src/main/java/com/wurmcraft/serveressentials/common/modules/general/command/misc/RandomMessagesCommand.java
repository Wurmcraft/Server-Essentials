package com.wurmcraft.serveressentials.common.modules.general.command.misc;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import joptsimple.internal.Strings;

@ModuleCommand(module = "General", name = "RandomMessages")
public class RandomMessagesCommand {

  public static final Random RAND = new Random();
  public static List<String> randomMessage = new ArrayList<>();

  @Command(
      args = {},
      usage = {},
      canConsoleUse = true)
  public void displayMessage(ServerPlayer sender) {
    if (randomMessage.size() == 0) {
      loadMessages();
    }
    int msg = RAND.nextInt(randomMessage.size());
    ChatHelper.sendToAll(randomMessage.get(msg));
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"line"},
      isSubCommand = true,
      canConsoleUse = true,
      subCommandAliases = {"Del", "D", "Remove", "Rem", "R"})
  public void Delete(ServerPlayer player, int line) {
    String removedLine = randomMessage.remove(line);
    ChatHelper.send(
        player.sender,
        player.lang.COMMAND_RANDOM_MESSAGE_DELETED.replaceAll("\\{@MESSAGE@}", removedLine));
    saveMessages();
  }

  @Command(
      args = {CommandArgument.STRING_ARR},
      usage = {"rule"},
      isSubCommand = true,
      canConsoleUse = true,
      subCommandAliases = {"Create", "Crea", "Cre", "C", "A", "Ad"})
  public void Add(ServerPlayer player, String... rule) {
    randomMessage.add(Strings.join(rule, " "));
    ChatHelper.send(player.sender, player.lang.COMMAND_RANDOM_MESSAGE_CREATED);
    saveMessages();
  }

  private static void loadMessages() {
    File rulesFile =
        new File(
            ConfigLoader.SAVE_DIR
                + File.separator
                + "Modules"
                + File.separator
                + "random-messages.txt");
    if (rulesFile.exists()) {
      try {
        randomMessage = Files.readAllLines(rulesFile.toPath());
      } catch (IOException e) {
        ServerEssentials.LOG.error("Failed to read random-messages.txt");
        ServerEssentials.LOG.error(e.getMessage());
      }
    } else {
      createDefaultMessages();
    }
  }

  private static void createDefaultMessages() {
    randomMessage.add("Join the Cult of NepNep");
    randomMessage.add("Wurmatron Rules!");
    saveMessages();
  }

  private static void saveMessages() {
    File rulesFile =
        new File(
            ConfigLoader.SAVE_DIR
                + File.separator
                + "Modules"
                + File.separator
                + "random-messages.txt");
    try {
      Files.write(
          rulesFile.toPath(),
          Strings.join(randomMessage, "\n").getBytes(),
          StandardOpenOption.WRITE);
    } catch (Exception e) {
      ServerEssentials.LOG.error("Failed to create rules.txt");
      ServerEssentials.LOG.error(e.getMessage());
    }
  }
}
