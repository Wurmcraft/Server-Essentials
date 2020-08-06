package com.wurmcraft.serveressentials.forge.modules.general.command.weather;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "Rain")
public class RainCommand {

  @Command(inputArguments = {})
  public void rain(ICommandSender sender) {
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setRaining(true);
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setThundering(false);
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setRainTime(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).rand.nextInt(168000) + 12000);
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_RAIN);
  }
}
