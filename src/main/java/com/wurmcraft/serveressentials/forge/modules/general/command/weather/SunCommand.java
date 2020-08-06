package com.wurmcraft.serveressentials.forge.modules.general.command.weather;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "Thunder", aliases = {"Storm"})
public class SunCommand {

  @Command(inputArguments = {})
  public void storm(ICommandSender sender) {
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setRaining(false);
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setThundering(false);
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setRainTime(0);
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SUN);
  }
}
