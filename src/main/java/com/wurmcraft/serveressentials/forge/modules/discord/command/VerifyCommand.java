package com.wurmcraft.serveressentials.forge.modules.discord.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.rest.DiscordToken;
import com.wurmcraft.serveressentials.forge.modules.discord.DiscordModule;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "Discord", name = "Verify", aliases = {"VerifyCode"})
public class VerifyCommand {

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"Code"})
  public void verifyCode(ICommandSender sender, String verifyCode) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      DiscordToken[] tokens = RestRequestHandler.Discord.getTokens();
      if (tokens != null && tokens.length > 0) {
        for (DiscordToken token : tokens) {
          if (token.token.equals(verifyCode)) {
            GlobalPlayer globalData = RestRequestHandler.User
                .getPlayer(player.getGameProfile().getId().toString());
            System.out.println("");
            globalData.discordID = token.id;
            RestRequestHandler.User
                .overridePlayer(player.getGameProfile().getId().toString(), globalData);
            ChatHelper
                .sendMessage(sender, PlayerUtils.getLanguage(sender).DISCORD_VERIFIED);
            for (String code : DiscordModule.config.commandUponVerify) {
              FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()
                  .executeCommand(
                      FMLCommonHandler.instance().getMinecraftServerInstance(),
                      code.replaceAll("%PLAYER%", player.getDisplayNameString()));
            }
            return;
          }
        }
      }
      ChatHelper
          .sendMessage(sender, PlayerUtils.getLanguage(sender).DISCORD_INVALID_CODE);
    }
  }
}
