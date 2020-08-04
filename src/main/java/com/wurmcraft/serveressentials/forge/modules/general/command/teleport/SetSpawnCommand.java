package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "SetSpawn")
public class SetSpawnCommand {

  @Command(inputArguments = {})
  public void setSpawn(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      SECore.config.spawn = new LocationWrapper(player.posX, player.posY, player.posZ,
          player.dimension);
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_SETSPAWN);
      try {
        Files.write(new File(SAVE_DIR + File.separator + "Global.json").toPath(),
            GSON.toJson(SECore.config).getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
