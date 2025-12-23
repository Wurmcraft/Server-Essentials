package com.wurmcraft.serveressentials.common.modules.protect.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.models.TrustInfo;
import com.wurmcraft.serveressentials.common.modules.protect.utils.ProtectionHelper;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(
    module = "Claim",
    name = "Trust",
    defaultAliases = {"Storage"})
public class TrustCommand {

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"user"})
  public void trust(ServerPlayer player, EntityPlayer trustee) {
    Claim claim = ProtectionHelper.getClaim(player.player.getPosition(), player.player.dimension);
    if (claim != null) {
      if (claim.owner.equalsIgnoreCase(player.player.getGameProfile().getId().toString())) {
        TrustInfo[] trustInfo = claim.trust;
        if (trustInfo == null) {
          TrustInfo newTrust =
              new TrustInfo(
                  trustee.getGameProfile().getId().toString(),
                  new TrustInfo.Action[] {
                    TrustInfo.Action.INTERACT, TrustInfo.Action.BREAK, TrustInfo.Action.PLACE
                  });
          trustInfo = new TrustInfo[] {newTrust};
          claim.trust = trustInfo;
        } else {
          TrustInfo newTrust =
              new TrustInfo(
                  trustee.getGameProfile().getId().toString(),
                  new TrustInfo.Action[] {
                    TrustInfo.Action.INTERACT, TrustInfo.Action.BREAK, TrustInfo.Action.PLACE
                  });
          List<TrustInfo> list = Arrays.asList(trustInfo);
          list.add(newTrust);
          claim.trust = list.toArray(new TrustInfo[0]);
        }
        ProtectionHelper.update(claim, player.player.dimension);
        ChatHelper.send(player.sender, player.lang.COMMAND_TRUST_ADDED);
      } else {
        ChatHelper.send(player.sender, player.lang.COMMAND_TRUST_DONT_OWN);
      }
    } else {
      ChatHelper.send(player.sender, player.lang.COMMAND_TRUST_NONE);
    }
  }
}
