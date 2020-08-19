package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "SetSpawn")
public class SetSpawnCommand {

  @Command(inputArguments = {})
  public void setSpawn(ICommandSender sender) {
    setSpawn(sender, "spawn");
  }

  @Command(inputArguments = {CommandArguments.RANK}, inputNames = {"rank"})
  public void setSpawn(ICommandSender sender, Rank rank) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      GeneralModule.config.spawn.spawns
          .put(rank.getID(), new LocationWrapper(player.posX, player.posY, player.posZ,
              player.dimension));
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_SETSPAWN);
      SECore.dataHandler.registerData(DataKey.MODULE_CONFIG, GeneralModule.config);
    }
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"firstJoin, spawn"})
  public void setSpawn(ICommandSender sender, String name) {
    if (name.equalsIgnoreCase("firstJoin") || name.equalsIgnoreCase("spawn")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        GeneralModule.config.spawn.spawns
            .put(name, new LocationWrapper(player.posX, player.posY, player.posZ,
                player.dimension));
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_SETSPAWN);
        SECore.dataHandler.registerData(DataKey.MODULE_CONFIG, GeneralModule.config);
      }
    } else {
      name = "spawn";
      setSpawn(sender);
    }
  }
}
