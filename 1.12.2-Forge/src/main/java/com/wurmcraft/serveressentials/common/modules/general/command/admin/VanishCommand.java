package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.general.event.VanishEvent;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "General", name = "Vanish", defaultAliases = {"V"})
public class VanishCommand {

  @Command(args = {}, usage = {})
  public void vanish(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      boolean inVanish = VanishEvent.vanishedPlayers.contains(player);
      if (inVanish) {
        VanishEvent.vanishedPlayers.remove(player);
        VanishEvent.updateVanish(player, true);
        ChatHelper.send(sender, SECore.dataLoader.get(DataType.LANGUAGE,
            SECore.dataLoader.get(DataType.ACCOUNT,
                player.getGameProfile().getId().toString(), new Account()).lang,
            new Language()).COMMAND_VANISH_UNDO);
      } else {
        VanishEvent.vanishedPlayers.add(player);
        VanishEvent.updateVanish(player, false);
        ChatHelper.send(sender, SECore.dataLoader.get(DataType.LANGUAGE,
            SECore.dataLoader.get(DataType.ACCOUNT,
                player.getGameProfile().getId().toString(), new Account()).lang,
            new Language()).COMMAND_VANISH);
      }
    }
  }
}
