package com.wurmcraft.serveressentials.common.modules.discord.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.DiscordVerify;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator.HttpResponse;

@ModuleCommand(module = "Discord", name = "Verify")
public class VerifyCommand {

  @Command(args = {CommandArgument.STRING}, usage = {"code"}, canConsoleUse = false)
  public static void verify(ServerPlayer player, String code) {
    DiscordVerify verify = new DiscordVerify(code,
        player.player.getGameProfile().getId().toString(), player.player.getName(), null,
        null);
    try {
      HttpResponse response = RequestGenerator.post("api/discord", verify);
      if (response.status == 200) {
        DiscordVerify verifyedData = ServerEssentials.GSON.fromJson(response.response,
            DiscordVerify.class);
        if (!verifyedData.discordID.isEmpty()) {
          // TODO Handle Discord Verified commands
          // TODO Send verified to api
          player.global.discord_id = verifyedData.discordID;
          SECore.dataLoader.update(DataType.ACCOUNT,
              player.player.getGameProfile().getId().toString(), player.global);
          ChatHelper.send(player.player, player.lang.COMMAND_VERIFY);
          return;
        }
      }
    } catch (Exception e) {
    }
    ChatHelper.send(player.player, player.lang.COMMAND_VERIFY_FAILED);
  }

}
