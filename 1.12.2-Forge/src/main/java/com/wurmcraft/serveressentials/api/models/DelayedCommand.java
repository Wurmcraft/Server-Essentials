package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.command.CommandConfig;
import com.wurmcraft.serveressentials.common.command.SECommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;

public class DelayedCommand {

  public ServerPlayer userData;
  public ICommandSender sender;
  public String[] args;
  public CommandConfig config;
  public SECommand seCommand;
  public BlockPos pos;
  public long runAfter;

  public DelayedCommand(ServerPlayer userData, ICommandSender sender, String[] args,
      CommandConfig config, SECommand seCommand, BlockPos pos, long runAfter) {
    this.userData = userData;
    this.sender = sender;
    this.args = args;
    this.config = config;
    this.seCommand = seCommand;
    this.pos = pos;
    this.runAfter = runAfter;
  }
}
