package com.wurmcraft.serveressentials.common.modules.chat.command;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.DataWrapper;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.WSWrapper;
import com.wurmcraft.serveressentials.api.models.data_wrapper.DMMessage;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(
    module = "Chat",
    name = "DM",
    defaultAliases = {"Msg", "M", "Pm"})
public class DMCommand {

  @Command(
      args = {CommandArgument.STRING, CommandArgument.STRING_ARR},
      usage = {"player", "msg"})
  public void msg(ServerPlayer player, String otherPlayer, String[] msg) {
    msg(player, otherPlayer, String.join(" ", msg));
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.STRING},
      usage = {"player", "msg"})
  public void msg(ServerPlayer player, String otherPlayer, String msg) {
    String uuid = PlayerUtils.getUUIDForInput(otherPlayer);
    if (uuid != null) {
      EntityPlayer otherEntity = PlayerUtils.getFromUUID(uuid);
      if (otherEntity != null) { // Local Message
        ChatHelper.send(player.player, otherEntity, msg);
      } else if (SECore.dataLoader.getClass().equals(RestDataLoader.class)) { // Remote Message
        try {
          ChatHelper.lastMessageCache.put(uuid, player.player.getGameProfile().getId().toString());
          ServerEssentials.socketController.send(
              new WSWrapper(
                  201,
                  WSWrapper.Type.MESSAGE,
                  new DataWrapper(
                      "DM",
                      GSON.toJson(
                          new DMMessage(
                              player.player.getGameProfile().getId().toString(),
                              ChatHelper.getName(player.player, player.global),
                              ServerEssentials.config.general.serverID,
                              msg,
                              uuid)))));
        } catch (Exception e) {
          LOG.warn("Failed to send DM though bridge");
          e.printStackTrace();
        }
      }
    } else {
      ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
    }
  }
}
