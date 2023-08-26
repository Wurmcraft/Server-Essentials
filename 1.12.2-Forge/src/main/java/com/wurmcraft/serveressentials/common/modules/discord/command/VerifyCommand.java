package com.wurmcraft.serveressentials.common.modules.discord.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.DataWrapper;
import com.wurmcraft.serveressentials.api.models.DiscordVerify;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.WSWrapper;
import com.wurmcraft.serveressentials.api.models.WSWrapper.Type;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.discord.ConfigDiscord;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator.HttpResponse;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
          player.global.discord_id = verifyedData.discordID;
          SECore.dataLoader.update(DataType.ACCOUNT,
              player.player.getGameProfile().getId().toString(), player.global);
          ServerEssentials.socketController.send(new WSWrapper(200, Type.MESSAGE,
              new DataWrapper("DiscordVerify",
                  ServerEssentials.GSON.toJson(verifyedData))));
          // Run Config Commands
          for (String command : ((ConfigDiscord) SECore.moduleConfigs.get(
              "DISCORD")).verifyCommands) {
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()
                .executeCommand(FMLCommonHandler.instance().getMinecraftServerInstance(),
                    command.replaceAll("\\{USERNAME}",
                        player.player.getGameProfile().getName()));
          }
          ChatHelper.send(player.player, player.lang.COMMAND_VERIFY);
          return;
        }
      }
    } catch (Exception e) {
      ServerEssentials.LOG.warn(
          "Failed to send post request to API (" + e.getMessage() + ")");
    }
    ChatHelper.send(player.player, player.lang.COMMAND_VERIFY_FAILED);
  }

}
