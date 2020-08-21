package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.google.common.collect.ImmutableCollection;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

@ModuleCommand(moduleName = "General", name = "chunkloading")
public class ChunkLoadingCommand {

  @Command(inputArguments = {})
  public void all(ICommandSender sender) {
    ImmutableCollection<Ticket> tickets =
        ForgeChunkManager.getPersistentChunksFor(sender.getEntityWorld()).values();
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
    for (Ticket ticket : tickets) {
      ChatHelper.sendMessage(
          sender,
          TextFormatting.AQUA
              + "["
              + ticket.getChunkList().asList().get(0).getXStart() + ", " + ticket
              .getChunkList().asList().get(0).getZStart() + "]");
    }
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
  }
}
