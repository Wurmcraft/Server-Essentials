package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import java.util.Objects;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "TPAll")
public class TPAllCommand {

  @Command(inputArguments = {})
  public void tpAll(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      LocationWrapper loc = new LocationWrapper(player.posX, player.posY, player.posZ,
          player.dimension);
      for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance()
          .getPlayerList().getPlayers()) {
        TeleportUtils.teleportTo(p, loc);
      }
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPALL);
    }
  }

  @Command(inputArguments = {CommandArguments.RANK}, inputNames = {"Rank"})
  public void tpAllRank(ICommandSender sender, Rank rank) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      LocationWrapper loc = new LocationWrapper(player.posX, player.posY, player.posZ,
          player.dimension);
      for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance()
          .getPlayerList().getPlayers()) {
        if (rank.name
            .equalsIgnoreCase(Objects.requireNonNull(RankUtils.getRank(sender)).name)) {
          TeleportUtils.teleportTo(p, loc);
        }
      }
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPALL);
    }
  }
}
