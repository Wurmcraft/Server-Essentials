package com.wurmcraft.serveressentials.forge.server.command;

import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyConfig;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import java.util.*;

public class WrapperCommand extends CommandBase {

  private ICommand command;

  public WrapperCommand(ICommand command) {
    this.command = command;
  }

  @Override
  public String getName() {
    return command.getName();
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return command.getUsage(sender);
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    command.execute(server, sender, args);
  }

  @Override
  public int getRequiredPermissionLevel() {
    return super.getRequiredPermissionLevel();
  }

  @Override
  public List<String> getAliases() {
    return command.getAliases();
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return RankUtils.hasPermission(sender, "command." + command.getName());
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      String[] args, BlockPos targetPos) {
    return command.getTabCompletions(server, sender, args, targetPos);
  }

  @Override
  public boolean isUsernameIndex(String[] args, int index) {
    return command.isUsernameIndex(args, index);
  }
}