package com.wurmcraft.serveressentials.common.modules.donation.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.modules.donation.ConfigDonation;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(module = "Donate", name = "Donate", defaultAliases = "Support")
public class DonateCommand {

  @Command(
      args = {},
      usage = {},
      canConsoleUse = true)
  public void donate(ServerPlayer player) {
    ChatHelper.send(
        player.sender,
        TextFormatting.AQUA + ((ConfigDonation) SECore.moduleConfigs.get("DONATE")).donateURL);
  }
}
