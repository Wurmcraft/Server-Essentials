package com.wurmcraft.serveressentials.common.modules.general.command.weather;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "Sun")
public class SunCommand {

  @Command(
      args = {},
      usage = {},
      canConsoleUse = true)
  public void sun(ServerPlayer player) {
    if (player.player == null)
      for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
        world.getWorldInfo().setRaining(false);
        world.getWorldInfo().setRainTime(0);
        world.getWorldInfo().setThundering(false);
      }
    else {
      player.player.world.getWorldInfo().setRaining(false);
      player.player.world.getWorldInfo().setRainTime(0);
      player.player.world.getWorldInfo().setThundering(false);
    }
    ChatHelper.send(player.sender, player.lang.COMMAND_WEATHER_SUN);
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"dimension"},
      canConsoleUse = true)
  public void sun(ServerPlayer player, int dim) {
    try {
      World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim);
      world.getWorldInfo().setRaining(false);
      world.getWorldInfo().setRainTime(0);
      world.getWorldInfo().setThundering(false);
      ChatHelper.send(player.sender, player.lang.COMMAND_WEATHER_SUN);
    } catch (Exception e) {
      ChatHelper.send(player.sender, player.lang.INVALID_DIMENSION);
    }
  }
}
