package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "spawn")
public class SpawnCommand {

  @Command(inputArguments = {})
  public void spawn(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      Rank rank = RankUtils.getRank(player);
      if (rank != null && GeneralModule.config.spawn.spawns.get(rank.getID()) != null) {
        TeleportUtils
            .teleportTo(player, GeneralModule.config.spawn.spawns.get(rank.getID()));
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_SPAWN);
      } else if (GeneralModule.config.spawn.spawns.get("spawn") != null) {
        TeleportUtils.teleportTo(player, GeneralModule.config.spawn.spawns.get("spawn"));
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_SPAWN);
      } else {
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_SPAWN_NONE);
      }
    }
  }
}
