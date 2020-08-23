package com.wurmcraft.serveressentials.forge.modules.language.command;

import static com.wurmcraft.serveressentials.forge.modules.language.command.ReplyCommand.lastMessageCache;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.language.LanguageModule;
import com.wurmcraft.serveressentials.forge.modules.language.event.ChatEvents;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import java.util.UUID;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "Language", name = "msg", aliases = {"pm"})
public class MsgCommand {

  @Command(inputArguments = {CommandArguments.PLAYER,
      CommandArguments.STRING_ARR}, inputNames = {"player", "msg"})
  public void sendMesssage(ICommandSender sender, EntityPlayer otherPlayer,
      String[] msg) {
    String message = Strings.join(msg, " ");
    ChatHelper.sendMessage(sender,
        LanguageModule.config.messageFormat.replaceAll("%PLAYER%", "you")
            .replaceAll("%PLAYER2%", otherPlayer.getDisplayNameString())
            .replaceAll("%MESSAGE%", message));
    ChatHelper.sendMessage(otherPlayer, LanguageModule.config.messageFormat
        .replaceAll("%PLAYER%", sender.getDisplayName().getUnformattedText())
        .replaceAll("%PLAYER2%", "you").replaceAll("%MESSAGE%", message));
    String spyFormat = "[DM]: " + LanguageModule.config.messageFormat
        .replaceAll("%PLAYER%", sender.getDisplayName().getUnformattedText())
        .replaceAll("%PLAYER2%", otherPlayer.getDisplayNameString())
        .replaceAll("%MESSAGE%", message);
    ChatEvents.LOGGER.info(spyFormat);
    for (UUID uuid : SocialSpyCommand.socialSpy) {
      ChatHelper.sendMessage(
          FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
              .getPlayerByUUID(uuid), spyFormat);
    }
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      lastMessageCache.put(otherPlayer.getGameProfile().getId().toString(),
          ((EntityPlayer) sender.getCommandSenderEntity()).getGameProfile().getId()
              .toString());
    }
  }
}
