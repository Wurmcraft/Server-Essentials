package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@ModuleCommand(module = "General", name = "TpaHere")
public class TPAHereCommand {

  public static NonBlockingHashMap<EntityPlayer, EntityPlayer> activeRequests =
      new NonBlockingHashMap<>();

  @Command(
      args = {CommandArgument.PLAYER},
      usage = "player")
  public void tpaHere(ServerPlayer player, EntityPlayer otherPlayer) {
    LocalAccount localOther =
        SECore.dataLoader.get(
            DataLoader.DataType.LOCAL_ACCOUNT,
            otherPlayer.getGameProfile().getId().toString(),
            new LocalAccount());
    if (otherPlayer.getGameProfile().getId()
        .equals(player.player.getGameProfile().getId())) {
      ChatHelper.send(player.player, player.lang.COMMAND_TP_SELF);
      return;
    }
    if (!ChatHelper.isIgnored(localOther,
        player.player.getGameProfile().getId().toString())) {
      activeRequests.put(otherPlayer, player.player);
      Language otherLang =
          SECore.dataLoader.get(
              DataLoader.DataType.LANGUAGE,
              SECore.dataLoader.get(
                  DataLoader.DataType.ACCOUNT,
                  otherPlayer.getGameProfile().getId().toString(),
                  new Account())
                  .lang,
              new Language());
      ChatHelper.send(
          otherPlayer,
          otherLang.COMMAND_TPAHERE_OTHER.replaceAll(
              "\\{@PLAYER@}", player.player.getDisplayNameString()));
    }
    ChatHelper.send(
        player.player,
        player.lang.COMMAND_TPAHERE.replaceAll("\\{@PLAYER@}",
            otherPlayer.getDisplayNameString()));
  }
}
