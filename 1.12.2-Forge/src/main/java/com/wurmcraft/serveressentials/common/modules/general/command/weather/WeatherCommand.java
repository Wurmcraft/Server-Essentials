package com.wurmcraft.serveressentials.common.modules.general.command.weather;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "Weather")
public class WeatherCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = "sun, rain, storm",
      canConsoleUse = true)
  public void weather(ServerPlayer player, String type) {
    if (type.equalsIgnoreCase("sun")
        || type.equalsIgnoreCase("sunny")
        || type.equalsIgnoreCase("su")) {
      for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
        world.getWorldInfo().setRaining(false);
        world.getWorldInfo().setRainTime(0);
        world.getWorldInfo().setThundering(false);
      }
      ChatHelper.send(player.sender, player.lang.COMMAND_WEATHER_SUN);
    } else if (type.equalsIgnoreCase("rain") || type.equalsIgnoreCase("r")) {
      for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
        world.getWorldInfo().setRaining(true);
        world.getWorldInfo().setRainTime(world.rand.nextInt(168000) + 12000);
        world.getWorldInfo().setThundering(false);
      }
      ChatHelper.send(player.sender, player.lang.COMMAND_WEATHER_RAIN);
    } else if (type.equalsIgnoreCase("storm")
        || type.equalsIgnoreCase("st")
        || type.equalsIgnoreCase("lightning")
        || type.equalsIgnoreCase("light")) {
      for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
        world.getWorldInfo().setRaining(true);
        world.getWorldInfo().setRainTime(world.rand.nextInt(168000) + 12000);
        world.getWorldInfo().setThundering(true);
      }
      ChatHelper.send(player.sender, player.lang.COMMAND_WEATHER_STORM);
    }
  }
}
