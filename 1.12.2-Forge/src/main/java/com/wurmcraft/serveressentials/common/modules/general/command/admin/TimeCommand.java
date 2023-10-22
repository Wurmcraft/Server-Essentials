package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "Time")
public class TimeCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = {"time"},
      canConsoleUse = true)
  public void setTime(ServerPlayer player, String time) {
    if (time.equalsIgnoreCase("day")) {
      setTime(player, 1000);
    }
    if (time.equalsIgnoreCase("noon")) {
      setTime(player, 6000);
    }
    if (time.equalsIgnoreCase("dust")) {
      setTime(player, 12000);
    }
    if (time.equalsIgnoreCase("night")) {
      setTime(player, 14000);
    }
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"time"},
      canConsoleUse = true)
  public void setTime(ServerPlayer player, int time) {
    FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().setWorldTime(time);
    ChatHelper.send(player.sender, player.lang.COMMAND_TIME.replaceAll("\\{@TIME@}", "" + time));
  }
}
