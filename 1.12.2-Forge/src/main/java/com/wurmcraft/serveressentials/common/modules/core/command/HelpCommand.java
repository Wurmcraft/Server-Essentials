package com.wurmcraft.serveressentials.common.modules.core.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CustomCommand;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.command.SECommand;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "Core", name = "help", defaultAliases = {"?"})
public class HelpCommand {

  public static final int COMMANDS_PER_PAGE = 10;
  public static List<HelpLine> commands;

  @Command(args = {}, usage = {}, canConsoleUse = true)
  public void helpPage0(ServerPlayer player) {
    help(player, 1);
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"page"},
      canConsoleUse = true)
  public void help(ServerPlayer player, int page) {
    if (commands == null || commands.size() == 0) {
      commands = generateHelpLines();
    }
    if (page == 0) {
      page = 1;
    }
    List<HelpLine> playerSpecificHelp = getPlayerCommands(player);
    if (page > playerSpecificHelp.size() / COMMANDS_PER_PAGE) {
      page = playerSpecificHelp.size() / COMMANDS_PER_PAGE;
    }
    String spacer = player.lang.SPACER;
    String center = Integer.toString(page) + " / " + Math.round(Math.ceil(
        (float) playerSpecificHelp.size() / COMMANDS_PER_PAGE));
    int splitLocation = (spacer.length() / 2) - ((center.length() / 2) + 1);
    String header = spacer.substring(0, splitLocation) + " &6(&a" + center + "&6) "
        + spacer.substring(splitLocation);
    ChatHelper.send(player.sender, header);
    for (int c = 0; c < COMMANDS_PER_PAGE; c++) {
      int f = c * page;
      if (f >= playerSpecificHelp.size()) {
        break;
      }
      ChatHelper.send(player.sender, playerSpecificHelp.get(f).commandName);
    }
    ChatHelper.send(player.sender, player.lang.SPACER);
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
    String usageName = "&b" + command.getUsage(sender).replaceAll("\n", ",  &b");
    return new HelpLine(usageName, "", getPermNode(command));
  }

  public static String getPermNode(ICommand command) {
    if (command instanceof SECommand) {
      SECommand cmd = (SECommand) command;
      return cmd.config.permissionNode;
    }
    if (command instanceof CustomCommand) {
      CustomCommand cmd = (CustomCommand) command;
      return cmd.command.permissionNode;
    }
    return "command." + command.getName().toLowerCase();
  }

  private static List<HelpLine> getPlayerCommands(ServerPlayer player) {
    List<HelpLine> cmd = new ArrayList<>();
    for (HelpLine help : commands) {
      if (userHasPermissionToUseCommand(player, help.permNode)) {
        if (!isDuplicate(help, cmd)) {
          cmd.add(help);
        }
      }
    }
    return cmd;
  }

  private static boolean isDuplicate(HelpLine test, List<HelpLine> current) {
    for (HelpLine help : current) {
      if (help.commandName.equals(test.commandName)) {
        return true;
      }
    }
    return false;
  }

  private static boolean userHasPermissionToUseCommand(ServerPlayer player,
      String command) {
    if (player.sender instanceof EntityPlayer) {
      return RankUtils.hasPermission(player.global, command);
    } else {
      return true;
    }
  }

  private static class HelpLine {

    public String commandName;
    public String description;
    public String permNode;

    public HelpLine(String commandName, String description, String permNode) {
      this.commandName = commandName;
      this.description = description;
      this.permNode = permNode;
    }
  }
}
