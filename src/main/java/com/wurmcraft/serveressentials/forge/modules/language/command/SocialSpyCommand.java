package com.wurmcraft.serveressentials.forge.modules.language.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.UUID;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import org.cliffc.high_scale_lib.NonBlockingHashSet;

public class SocialSpyCommand {

  public static NonBlockingHashSet<UUID> socialSpy = new NonBlockingHashSet<>();

  @Command(inputArguments = {})
  public void socialSpy(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (socialSpy.contains(player.getGameProfile().getId())) {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).LANGUAGE_SOCIALSPY_DISABLED);
        socialSpy.remove(player.getGameProfile().getId());
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).LANGUAGE_SOCIALSPY_ENABLED);
        socialSpy.add(player.getGameProfile().getId());
      }
    }
  }

}
