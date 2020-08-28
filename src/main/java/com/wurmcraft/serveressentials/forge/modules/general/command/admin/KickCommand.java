package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "kick")
public class KickCommand {

  @Command(inputArguments = {CommandArguments.PLAYER})
  public void kick(ICommandSender sender, EntityPlayer player) {
    EntityPlayerMP playerMP = (EntityPlayerMP) player;
    playerMP.connection.disconnect(
        new TextComponentString(TextFormatting.RED + "You have been kicked!"));
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_KICK);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING_ARR})
  public void kick(ICommandSender sender, EntityPlayer player, String[] args) {
    EntityPlayerMP playerMP = (EntityPlayerMP) player;
    playerMP.connection.disconnect(new TextComponentString(Strings.join(args, " ")));
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_KICK);
  }

  @Command(inputArguments = {CommandArguments.STRING})
  public void kick(ICommandSender sender, String uuid) {
    EntityPlayerMP playerMP = null;
    for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      if (player.getGameProfile().getId().toString().equals(uuid)) {
        playerMP = player;
        break;
      }
    }
    if (playerMP != null) {
      kick(sender, playerMP);
    }
  }
}
