package com.wurmcraft.serveressentials.forge.modules.language.command;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Channel;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.NoSuchElementException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "Language", name = "channel", aliases = {"chat", "ch"})
public class ChannelCommand {

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"channel"})
  public void setChannel(ICommandSender sender, String channel) {
    if (channel.equalsIgnoreCase("list")) {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
      for (Channel ch : SECore.dataHandler
          .getDataFromKey(DataKey.CHANNEL, new Channel(null, null)).values()) {
        ChatHelper.sendClickMessage(sender, ch.name, "/ch " + ch.name);
      }
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
    } else {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        try {
          Channel ch = (Channel) SECore.dataHandler.getData(DataKey.CHANNEL, channel);
          if (RankUtils.hasPermission(sender, "language.channel." + ch.name)) {
            StoredPlayer playerData = PlayerUtils.get(player);
            playerData.server.channel = ch.name;
            SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).LANGUAGE_CHANNEL
                    .replaceAll("%CHANNEL%", ch.name));
          } else {
            TextComponentTranslation noPerms = new TextComponentTranslation(
                "commands.generic.permission", new Object[0]);
            noPerms.getStyle().setColor(TextFormatting.RED);
            ChatHelper.sendHoverMessage(sender, noPerms,
                TextFormatting.RED + "language.channel" + ch.name);
          }
        } catch (NoSuchElementException e) {
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).LANGUAGE_CHANNEL_NONE
                  .replaceAll("%CHANNEL%", channel));
        }
      }
    }
  }
}
