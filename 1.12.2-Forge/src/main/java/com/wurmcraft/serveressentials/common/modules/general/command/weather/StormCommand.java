package com.wurmcraft.serveressentials.common.modules.general.command.weather;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(
    module = "General",
    name = "Storm",
    defaultAliases = {"Lightning"})
public class StormCommand {

  @Command(
      args = {},
      usage = {},
      canConsoleUse = true)
  public void storm(ServerPlayer player) {
    if (player.player == null)
      for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
        world.getWorldInfo().setRaining(true);
        world.getWorldInfo().setRainTime(world.rand.nextInt(168000) + 12000);
        world.getWorldInfo().setThundering(true);
      }
    else {
      player.player.world.getWorldInfo().setRaining(true);
      player
          .player
          .world
          .getWorldInfo()
          .setRainTime(player.player.world.rand.nextInt(168000) + 12000);
      player.player.world.getWorldInfo().setThundering(true);
    }
    ChatHelper.send(player.sender, player.lang.COMMAND_WEATHER_STORM);
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"dimension"},
      canConsoleUse = true)
  public void storm(ServerPlayer player, int dim) {
    try {
      World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim);
      world.getWorldInfo().setRaining(true);
      world.getWorldInfo().setRainTime(world.rand.nextInt(168000) + 12000);
      world.getWorldInfo().setThundering(true);
      ChatHelper.send(player.sender, player.lang.COMMAND_WEATHER_STORM);
    } catch (Exception e) {
      ChatHelper.send(player.sender, player.lang.INVALID_DIMENSION);
    }
  }
}
