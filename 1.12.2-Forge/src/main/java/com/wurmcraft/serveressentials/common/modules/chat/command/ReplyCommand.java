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
import com.wurmcraft.serveressentials.common.data.ws.SocketController;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "Chat", name = "Reply", defaultAliases = "R")
public class ReplyCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = "msg")
  public void reply(ServerPlayer player, String msg) {
    if (ChatHelper.lastMessageCache.containsKey(
        player.player.getGameProfile().getId().toString())) {
      String lastMsg =
          ChatHelper.lastMessageCache.get(player.player.getGameProfile().getId().toString());
      EntityPlayer otherPlayer = PlayerUtils.getFromUUID(lastMsg);
      if (otherPlayer != null) ChatHelper.send(player.player, otherPlayer, msg);
      else if (SECore.dataLoader.getClass().equals(RestDataLoader.class)) {
        try {
          SocketController.send(
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
                              lastMsg)))));
          // TODO Send Confirmation Message
        } catch (Exception e) {
          LOG.warn("Failed to send DM though bridge");
          e.printStackTrace();
        }
      } else ChatHelper.send(player.sender, player.lang.COMMAND_REPLY_INVALID);
    } else ChatHelper.send(player.sender, player.lang.COMMAND_REPLY_INVALID);
  }

  @Command(
      args = {CommandArgument.STRING_ARR},
      usage = "msg")
  public void reply(ServerPlayer player, String[] msg) {
    reply(player, String.join(" ", msg));
  }
}
