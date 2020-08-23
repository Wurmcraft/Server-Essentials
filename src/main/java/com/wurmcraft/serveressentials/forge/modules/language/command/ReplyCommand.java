package com.wurmcraft.serveressentials.forge.modules.language.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.UUID;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@ModuleCommand(moduleName = "Language", name = "reply", aliases = {"r"})
public class ReplyCommand {

  public static NonBlockingHashMap<String, String> lastMessageCache = new NonBlockingHashMap<>();

  @Command(inputArguments = {CommandArguments.STRING_ARR}, inputNames = {"msg"})
  public void replyToMessage(ICommandSender sender, String[] msg) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      String message = Strings.join(msg, " ");
      if (lastMessageCache.containsKey(player.getGameProfile().getId().toString())) {
        FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()
            .executeCommand(sender, "/msg " + UsernameCache.getLastKnownUsername(
                UUID.fromString(
                    lastMessageCache.get(player.getGameProfile().getId().toString())))
                + " " + message);
      } else {
        ChatHelper
            .sendMessage(player, PlayerUtils.getLanguage(player).LANGUAGE_REPLY_NONE);
      }
    }
  }

}
