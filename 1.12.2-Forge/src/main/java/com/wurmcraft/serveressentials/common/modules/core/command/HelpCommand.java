package com.wurmcraft.serveressentials.common.modules.core.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;

import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.SECommand;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.*;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "Core", name = "help", defaultAliases = {"?"})
public class HelpCommand {

  public static final int COMMANDS_PER_PAGE = 10;
  public static List<HelpLine> commands;

  @Command(args =  {}, usage = {},canConsoleUse = true)
  public void helpPage0(ServerPlayer player) {
    help(player, 0);
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"page"},
      canConsoleUse = true)
  public void help(ServerPlayer player, int page) {
    if (commands == null || commands.size() == 0) {
      commands = generateHelpLines();
    }
    HelpLine[] commandsOnPage = getListOfCommands(player, page * COMMANDS_PER_PAGE,
        COMMANDS_PER_PAGE);
    ChatHelper.send(player.sender, player.lang.SPACER);
    for (HelpLine command : commandsOnPage) {
      ChatHelper.send(player.sender,
          player.lang.COMMAND_HELP_FORMAT.replaceAll("\\{@COMMAND@}",
                  command.commandName)
              .replaceAll("\\{@DESCRIPTION@}", command.description));
    }
  }

  private static List<HelpLine> generateHelpLines() {
    List<HelpLine> commands = new ArrayList<>();
    for (ICommand command : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getCommandManager().getCommands().values()) {
      commands.add(
          getHelpInfo(FMLCommonHandler.instance().getMinecraftServerInstance(), command));
    }
    return commands;
  }

  private static HelpLine getHelpInfo(ICommandSender sender, ICommand command) {
    String usageName = command.getUsage(sender);
    return new HelpLine(usageName, "");
  }

  private static HelpLine[] getListOfCommands(ServerPlayer player, int startingPoint,
      int count) {
    HelpLine[] possibleCommands = Arrays.copyOfRange(commands.toArray(new HelpLine[0]),
        startingPoint, startingPoint + count);
    List<HelpLine> userValidCommands = new ArrayList<>();
    for (HelpLine command : possibleCommands) {
      if (userHasPermissionToUseCommand(player, command.commandName)) {
        userValidCommands.add(command);
      }
    }
    while (userValidCommands.size() != count) {
      HelpLine[] nextCommands = getListOfCommands(player, startingPoint + count + 1, 1);
      if (nextCommands.length == 0) {
        break; // No more commands
      }
      userValidCommands.add(nextCommands[0]);
    }
    return userValidCommands.toArray(new HelpLine[0]);
  }

  private static boolean userHasPermissionToUseCommand(ServerPlayer player,
      String command) {
    return true;
  }

  private static class HelpLine {

    public String commandName;
    public String description;

    public HelpLine(String commandName, String description) {
      this.commandName = commandName;
      this.description = description;
    }
  }

}
