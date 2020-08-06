package com.wurmcraft.serveressentials.forge.modules.core.utils;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.config.GlobalConfig;
import com.wurmcraft.serveressentials.forge.modules.core.CoreModule;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.command.CustomCommand;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParams;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParams.RankParams;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParamsConfig;
import com.wurmcraft.serveressentials.forge.server.command.json.CustomCommandJson;
import com.wurmcraft.serveressentials.forge.server.command.json.CustomCommandJson.Command;
import com.wurmcraft.serveressentials.forge.server.command.json.CustomCommandJson.CommandFunc;
import com.wurmcraft.serveressentials.forge.server.json.MessagesConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import org.apache.logging.log4j.util.Strings;

public class CoreUtils {

  public static CommandParamsConfig loadParamsConfig() {
    CommandParamsConfig config;
    try {
      config = GSON.fromJson(Strings.join(Files.readAllLines(
          new File(
              SAVE_DIR + File.separator + "Misc" + File.separator
                  + "CommandSettings.json")
              .toPath()),
          '\n'), CommandParamsConfig.class);
      forceLowerCase(config);
      return config;
    } catch (IOException f) {
      config = new CommandParamsConfig();
      try {
        File SAVE = new File(SAVE_DIR + File.separator + "Misc");
        if (!SAVE.exists()) {
          SAVE.mkdirs();
        }
        Files.write(new File(SAVE + File.separator + "CommandSettings.json")
                .toPath(),
            GSON.toJson(config).getBytes());
      } catch (Exception g) {
        ServerEssentialsServer.LOGGER
            .fatal("Failed to save CommandSettings.json, (Using default values)");
      }
      forceLowerCase(config);
    }
    return config;
  }

  public static void forceLowerCase(CommandParamsConfig config) {
    for (String commands : config.commands.keySet()) {
      String[] rankKeys = config.commands.get(commands).ranks.keySet()
          .toArray(new String[0]);
      for (int index = 0; index < rankKeys.length; index++) {
        RankParams params = config.commands.get(commands).ranks.get(rankKeys[index]);
        String key = rankKeys[index];
        config.commands.get(commands).ranks.remove(key);
        config.commands.get(commands).ranks.put(key.toLowerCase(), params);
      }
    }
  }

  public static GlobalConfig loadGlobalConfig() {
    GlobalConfig config;
    try {
      config = GSON.fromJson(Strings.join(Files.readAllLines(
          new File(SAVE_DIR + File.separator + "Global.json").toPath()),
          '\n'), GlobalConfig.class);
      return config;
    } catch (IOException f) {
      config = new GlobalConfig();
      try {
        if (!SAVE_DIR.exists()) {
          SAVE_DIR.mkdirs();
        }
        Files.write(new File(SAVE_DIR + File.separator + "Global.json").toPath(),
            GSON.toJson(config).getBytes());
      } catch (Exception g) {
        ServerEssentialsServer.LOGGER
            .fatal("Failed to save Global.json, (Using default values)");
      }
    }
    return config;
  }

  public static CommandParams getParams(String node) {
    for (String command : CoreModule.commandConfig.commands.keySet()) {
      if (command.equalsIgnoreCase(node)) {
        return CoreModule.commandConfig.commands.get(command);
      }
    }
    return null;
  }

  public static MessagesConfig loadMessagesConfig() {
    MessagesConfig config;
    try {
      config = GSON.fromJson(Strings.join(Files.readAllLines(
          new File(
              SAVE_DIR + File.separator + "Misc" + File.separator
                  + "Messages.json")
              .toPath()),
          '\n'), MessagesConfig.class);
      return config;
    } catch (IOException f) {
      config = new MessagesConfig();
      try {
        File SAVE = new File(SAVE_DIR + File.separator + "Misc");
        if (!SAVE.exists()) {
          SAVE.mkdirs();
        }
        Files.write(new File(SAVE + File.separator + "Messages.json")
                .toPath(),
            GSON.toJson(config).getBytes());
      } catch (Exception g) {
        ServerEssentialsServer.LOGGER
            .fatal("Failed to save Messages.json, (Using default values)");
      }
    }
    return config;
  }

  public static CustomCommandJson[] loadCustomCommands() {
    List<CustomCommandJson> commandJsons = new ArrayList<>();
    File baseDir = new File(
        SAVE_DIR + File.separator + "Misc" + File.separator + "Custom-Commands");
    if (baseDir.exists()) {
      for (File file : baseDir.listFiles()) {
        try {
          commandJsons.add(GSON.fromJson(Strings.join(Files.readAllLines(file.toPath()),
              '\n'), CustomCommandJson.class));
        } catch (Exception e) {
          ServerEssentialsServer.LOGGER
              .error("Unable to load Command '" + file.getName() + "'");
        }
      }
    } else {
      createDefaultCustomCommands();
    }
    return commandJsons.toArray(new CustomCommandJson[0]);
  }

  private static void createDefaultCustomCommands() {
    File baseDir = new File(
        SAVE_DIR + File.separator + "Misc" + File.separator + "Custom-Commands");
    if (!baseDir.exists()) {
      baseDir.mkdirs();
    }
    saveCustomCommand(new CustomCommandJson("Website", new String[]{"Site"},
        new CommandFunc[]{new CommandFunc(Command.MESSAGE, new String[]{
            "&3https://www.curseforge.com/minecraft/mc-mods/server-essentials"})}));
    saveCustomCommand(new CustomCommandJson("Web", new String[]{"Site"},
        new CommandFunc[]{new CommandFunc(Command.COMMAND, new String[]{"Website"})}));
    saveCustomCommand(new CustomCommandJson("Discord", new String[]{}, new CommandFunc[]{
        new CommandFunc(Command.MESSAGE, new String[]{"&3https://discord.gg/n6RFDUc"})}));
  }

  private static void saveCustomCommand(CustomCommandJson json) {
    File baseDir = new File(
        SAVE_DIR + File.separator + "Misc" + File.separator + "Custom-Commands");
    if (!baseDir.exists()) {
      baseDir.mkdirs();
    }
    try {
      File customCommand = new File(baseDir + File.separator + json.name + ".json");
      if (!customCommand.exists()) {
        customCommand.createNewFile();
      }
      Files.write(customCommand.toPath(),
          GSON.toJson(json).getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static CustomCommand[] generateCustomCommands() {
    List<CustomCommand> commands = new ArrayList<>();
    if (CoreModule.customCommands.length > 0) {
      for (CustomCommandJson json : CoreModule.customCommands) {
        commands.add(new CustomCommand(json));
      }
    }
    return commands.toArray(new CustomCommand[0]);
  }
}
