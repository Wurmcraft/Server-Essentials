package com.wurmcraft.serveressentials.forge.modules.general.command.weather;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "Thunder", aliases = {"Storm"})
public class ThunderCommand {

  @Command(inputArguments = {})
  public void storm(ICommandSender sender) {
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setRaining(true);
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setThundering(true);
    FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldInfo().setRainTime(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).rand.nextInt(168000) + 12000);
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_THUNDER);
  }
}
