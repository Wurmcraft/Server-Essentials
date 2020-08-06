package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.io.File;
import java.util.UUID;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "DeletePlayerFile", aliases = {"DPF"})
public class DPFCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void deletePlayerFile(ICommandSender sender, EntityPlayer player) {
    deletePlayerFile(sender, player.getGameProfile().getId().toString());
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"Player"})
  public void deletePlayerFile(ICommandSender sender, String uuid) {
    File playerFile = new File(
        FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory(),
        File.separator + FMLCommonHandler.instance().getMinecraftServerInstance()
            .getFolderName() + File.separator + "playerdata" + File.separator + uuid
            + ".dat");
    if (playerFile.exists()) {
      ServerEssentialsServer.LOGGER.info("Deleting PlayerData '" + uuid + "'");
      try {
        if (playerFile.delete()) {
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).GENERAL_DPF.replaceAll("%PLAYER%",
                  UsernameCache.getLastKnownUsername(UUID.fromString(uuid))));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_PLAYER_NONE
          .replaceAll("%PLAYER%", uuid));
    }
  }

}
