package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.google.common.collect.ImmutableCollection;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

@ModuleCommand(module = "General", name = "ChunkLoading")
public class ChunkLoadingCommand {

  @Command(
      args = {},
      usage = {})
  public void all(ServerPlayer player) {
    ImmutableCollection<Ticket> tickets =
        ForgeChunkManager.getPersistentChunksFor(player.player.getEntityWorld()).values();
    ChatHelper.send(player.sender, player.lang.SPACER);
    for (Ticket ticket : tickets) {
      ChatHelper.send(
          player.sender,
          TextFormatting.AQUA
              + "["
              + ticket.getChunkList().asList().get(0).getXStart()
              + ", "
              + ticket.getChunkList().asList().get(0).getZStart()
              + "]");
    }
    ChatHelper.send(player.sender, player.lang.SPACER);
  }
}
