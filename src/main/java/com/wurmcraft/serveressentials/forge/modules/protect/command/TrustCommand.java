package com.wurmcraft.serveressentials.forge.modules.protect.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.protect.data.ClaimData;
import com.wurmcraft.serveressentials.forge.modules.protect.utils.ProtectionHelper;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "Protect", name = "trust", aliases = {"t"})
public class TrustCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"player"})
  public void trustPlayer(ICommandSender sender, EntityPlayer otherPlayer) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ClaimData claim = ProtectionHelper.getClaim(player.getPosition(), player.dimension);
      if (claim != null) {
        if (claim.trusted == null || claim.trusted.length == 0) {
          claim.trusted = new String[]{otherPlayer.getGameProfile().getId().toString()};
          ProtectionHelper.saveClaimData(claim, player.dimension);
        } else {
          List<String> trusted = new ArrayList<>();
          Collections.addAll(trusted, claim.trusted);
          trusted.add(otherPlayer.getGameProfile().getId().toString());
          claim.trusted = trusted.toArray(new String[0]);
          ProtectionHelper.saveClaimData(claim, player.dimension);
        }
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).PROTECT_TRUST
            .replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()));
      } else {
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).PROTECT_TRUST_FAIL);
      }
    }
  }
}
