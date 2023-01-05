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
import joptsimple.internal.Strings;

@ModuleCommand(module = "General", name = "Rules")
public class RulesCommand {

  public static List<String> rules = new ArrayList<>();

  @Command(args = {}, usage = {})
  public void displayRules(ServerPlayer sender) {
    if (rules.size() == 0) {
      loadRules();
    }
    for (String rule : rules) {
      ChatHelper.send(sender.sender, rule);
    }
  }

  @Command(args = {CommandArgument.INTEGER}, usage = {
      "line"}, isSubCommand = true, subCommandAliases = {"Del", "D", "Remove", "Rem",
      "R"})
  public void Delete(ServerPlayer player, int line) {
    String removedLine = rules.remove(line);
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RULES_DELETED.replaceAll("\\{@LINE@}", removedLine));
    saveRules();
  }

  @Command(args = {CommandArgument.STRING_ARR}, usage = {
      "rule"}, isSubCommand = true, subCommandAliases = {"Create", "Crea", "Cre", "C",
      "A", "Ad"})
  public void Add(ServerPlayer player, String... rule) {
    rules.add(Strings.join(rule, " "));
    ChatHelper.send(player.sender, player.lang.COMMAND_RULES_CREATED);
    saveRules();
  }

  private static void loadRules() {
    File rulesFile = new File(
        ConfigLoader.SAVE_DIR + File.separator + "Modules" + File.separator
            + "rules.txt");
    if (rulesFile.exists()) {
      try {
        rules = Files.readAllLines(rulesFile.toPath());
      } catch (IOException e) {
        ServerEssentials.LOG.error("Failed to read rules.txt");
        ServerEssentials.LOG.error(e.getMessage());
      }
    } else {
      createDefaultRiles();
    }
  }

  private static void createDefaultRiles() {
    rules.add("1. Use Common Since!");
    rules.add("2. Some other random rule!");
    saveRules();
  }

  private static void saveRules() {
    File rulesFile = new File(
        ConfigLoader.SAVE_DIR + File.separator + "Modules" + File.separator
            + "rules.txt");
    try {
      Files.write(rulesFile.toPath(), Strings.join(rules, "\n").getBytes(),
          StandardOpenOption.WRITE);
    } catch (Exception e) {
      ServerEssentials.LOG.error("Failed to create rules.txt");
      ServerEssentials.LOG.error(e.getMessage());
    }
  }
}