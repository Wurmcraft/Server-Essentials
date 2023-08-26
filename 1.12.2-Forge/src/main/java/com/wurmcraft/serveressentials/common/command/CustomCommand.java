package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.CustomCommandJson;
import com.wurmcraft.serveressentials.api.command.CustomCommandJson.CommandFunc;
import com.wurmcraft.serveressentials.api.command.CustomCommandJson.CommandType;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CustomCommand extends CommandBase {

  public static final File SAVE_DIR = new File(
      ConfigLoader.SAVE_DIR + File.separator + "Commands" + File.separator + "Custom");

  public CustomCommandJson command;

  public CustomCommand(CustomCommandJson command) {
    this.command = command;
  }

  @Override
  public String getName() {
    return command.name;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/" + getName();
  }

  @Override
  public List<String> getAliases() {
    List<String> aliases = new ArrayList<>();
    for (String a : command.aliases) {
      aliases.add(a);
      aliases.add(a.toLowerCase());
      aliases.add(a.toUpperCase());
    }
    aliases.add(getName().toLowerCase());
    aliases.add(getName().toUpperCase());
    return aliases;
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    if (sender instanceof EntityPlayer) {
      Account account = SECore.dataLoader.get(DataType.ACCOUNT,
          ((EntityPlayer) sender).getGameProfile().getId().toString(), new Account());
      // Min Rank Check
      if (command.minRank != null && !command.minRank.isEmpty()
          && RankUtils.isGreaterThan(command.minRank, account.rank)) {
        return false;
      }
      return RankUtils.hasPermission(account, command.permissionNode);
    } else if (command.canConsoleRun) {
      return true;
    } else {
      Language lang = (Language) SECore.dataLoader.get(DataType.LANGUAGE,
          ((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang);
      ChatHelper.send(sender, lang.PLAYER_ONLY);
    }
    return super.checkPermission(server, sender);
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    for (CommandFunc function : command.functions) {
      if (function.type.equals(CommandType.COMMAND)) {
        for (String cmd : function.values) {
          FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()
              .executeCommand(sender, cmd);
        }
      }
      if (function.type.equals(CommandType.MESSAGE)) {
        for (String message : function.values) {
          ChatHelper.send(sender, ChatHelper.replaceColor(message));
        }
      }
      if (function.type.equals(CommandType.CONSOLE_COMMAND)) {
        for (String cmd : function.values) {
          FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()
              .executeCommand(server, cmd);
        }
      }
    }
  }

  public static List<CustomCommandJson> loadCustomCommands() {
    if (!SAVE_DIR.exists()) {
      SAVE_DIR.mkdirs();
      createDefaultCommands();
    }
    List<CustomCommandJson> customCommands = new ArrayList<>();
    for (File command : Objects.requireNonNull(SAVE_DIR.listFiles())) {
      try {
        customCommands.add(
            ServerEssentials.GSON.fromJson(String.join("\n",
                    Files.readAllLines(command.toPath()).toArray(new String[0])),
                CustomCommandJson.class));
      } catch (Exception e) {
        ServerEssentials.LOG.warn(
            "Failed to load custom command '" + command.getName() + "' (" + e.getMessage()
                + ")");
      }
    }
    return customCommands;
  }

  public static void createDefaultCommands() {
    CustomCommandJson websiteComamnd = new CustomCommandJson("Website",
        new String[]{"Web", "Site"},
        new CommandFunc[]{new CommandFunc(CommandType.MESSAGE,
            new String[]{"https://wiki.wurmatron.io/serveressentials",
                "https://github.com/Wurmcraft/Server-Essentials"})}, "",
        "customcommand.website", false, new String[0], new String[0], new String[0],
        true);
    try {
      Files.write(new File(SAVE_DIR + File.separator + "website.json").toPath(),
          ServerEssentials.GSON.toJson(websiteComamnd).getBytes(),
          StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
    } catch (Exception e) {
      ServerEssentials.LOG.warn(
          "Failed to save custom command 'website' (" + e.getMessage() + ")");
    }
  }
}
