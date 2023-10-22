package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.general.event.GeneralEvents;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

@ModuleCommand(
    module = "General",
    name = "Freeze",
    defaultAliases = {"StopMoving", "Bubble"})
public class FreezeCommand {

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"Player"})
  public void freezePlayer(ServerPlayer sender, EntityPlayer player) {
    boolean isFrozen = GeneralEvents.isFrozen(player);
    if (isFrozen) {
      GeneralEvents.removeFrozen(player);
      ChatHelper.send(
          sender.sender,
          sender.lang.COMMAND_FREEZE_UNDO_OTHER.replaceAll(
              "\\{@PLAYER@}",
              ChatHelper.getName(
                  player,
                  SECore.dataLoader.get(
                      DataType.ACCOUNT,
                      player.getGameProfile().getId().toString(),
                      new Account()))));
      ChatHelper.send(
          player,
          SECore.dataLoader.get(
                  DataType.LANGUAGE,
                  SECore.dataLoader.get(
                          DataType.ACCOUNT,
                          player.getGameProfile().getId().toString(),
                          new Account())
                      .lang,
                  new Language())
              .COMMAND_FREEZE_UNDO);
    } else {
      GeneralEvents.addFrozen(player, new BlockPos(player.posX, player.posY, player.posZ));
      ChatHelper.send(
          sender.sender,
          sender.lang.COMMAND_FREEZE_OTHER.replaceAll(
              "\\{@PLAYER@}",
              ChatHelper.getName(
                  player,
                  SECore.dataLoader.get(
                      DataType.ACCOUNT,
                      player.getGameProfile().getId().toString(),
                      new Account()))));
      ChatHelper.send(
          player,
          SECore.dataLoader.get(
                  DataType.LANGUAGE,
                  SECore.dataLoader.get(
                          DataType.ACCOUNT,
                          player.getGameProfile().getId().toString(),
                          new Account())
                      .lang,
                  new Language())
              .COMMAND_FREEZE);
    }
  }
}
