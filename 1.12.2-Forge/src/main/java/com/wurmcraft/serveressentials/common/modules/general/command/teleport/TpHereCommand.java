package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(
    module = "General",
    name = "TpHere",
    defaultAliases = {"Tph"})
public class TpHereCommand {

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void teleportHere(ServerPlayer player, EntityPlayer otherPlayer) {
    ChatHelper.send(
        player.player,
        player.lang.COMMAND_TPHERE.replaceAll("\\{@PLAYER@}",
            otherPlayer.getDisplayNameString()));
    Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
    TeleportUtils.teleportTo(
        (EntityPlayerMP) otherPlayer,
        SECore.dataLoader.get(
            DataLoader.DataType.LOCAL_ACCOUNT,
            otherPlayer.getGameProfile().getId().toString(),
            new LocalAccount()),
        new Location(
            player.player.posX,
            player.player.posY,
            player.player.posZ,
            player.player.dimension,
            otherPlayer.rotationPitch,
            otherPlayer.rotationYaw),
        false);
    ChatHelper.send(otherPlayer, otherLang.COMMAND_TPHERE_OTHER);
  }
}
