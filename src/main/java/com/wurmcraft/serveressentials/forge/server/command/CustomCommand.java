package com.wurmcraft.serveressentials.forge.server.command;

import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.command.json.CustomCommandJson;
import com.wurmcraft.serveressentials.forge.server.command.json.CustomCommandJson.Command;
import com.wurmcraft.serveressentials.forge.server.command.json.CustomCommandJson.CommandFunc;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;


public class CustomCommand extends CommandBase {

  public CustomCommandJson commandConfig;

  public CustomCommand(
      CustomCommandJson commandConfig) {
    this.commandConfig = commandConfig;
  }

  @Override
  public String getName() {
    return commandConfig.name;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/" + commandConfig.name;
  }

  @Override
  public List<String> getAliases() {
    List<String> aliases = new ArrayList<>();
    for (String a : commandConfig.aliases) {
      aliases.add(a);
      aliases.add(a.toLowerCase());
      aliases.add(a.toUpperCase());
    }
    aliases.add(commandConfig.name.toLowerCase());
    aliases.add(commandConfig.name.toUpperCase());
    return aliases;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    for (CommandFunc func : commandConfig.functions) {
      if (func.type == Command.COMMAND) {
        for (String cmd : func.values) {
          FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()
              .executeCommand(sender, cmd);
        }
      } else if (func.type == Command.MESSAGE) {
        for (String msg : func.values) {
          ChatHelper.sendMessage(sender, msg);
        }
      } else if (func.type == Command.CONSOLE_COMMAND) {
        for (String cmd : func.values) {
          FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()
              .executeCommand(server, cmd);
        }
      }
    }
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return RankUtils.hasPermission(sender, "customcommand." + commandConfig.name);
  }
}
