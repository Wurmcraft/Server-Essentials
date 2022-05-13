package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.UsernameCache;

@ModuleCommand(
    module = "General",
    name = "TpOffline",
    defaultAliases = {"OfflineTP", "OffTp", "OTp"})
public class TpOfflineCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = {"player"})
  public void tpOffline(ServerPlayer player, String user) {
    String uuid = PlayerUtils.getUUIDForInput(user);
    LocalAccount local =
        SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, uuid, new LocalAccount());
    if (local != null) {
      if (TeleportUtils.teleportTo(
          (EntityPlayerMP) player.player, player.local, local.lastLocation))
        ChatHelper.send(
            player.sender,
            player.lang.COMMAND_TPOFFLINE.replaceAll(
                "\\{@PLAYER@}", UsernameCache.getLastKnownUsername(UUID.fromString(uuid))));
    } else ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
  }
}
