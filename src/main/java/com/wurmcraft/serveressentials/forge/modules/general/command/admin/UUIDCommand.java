package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.UsernameCache;

@ModuleCommand(moduleName = "General", name = "UUID")
public class UUIDCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void lookupUUID(ICommandSender sender, EntityPlayer player) {
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_UUID
        .replaceAll("%UUID%", player.getGameProfile().getId().toString()));
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"Player"})
  public void lookupUUID(ICommandSender sender, String player) {
    ChatHelper.sendMessage(sender,
        PlayerUtils.getLanguage(sender).GENERAL_UUID.replaceAll("%UUID%",
            Objects
                .requireNonNull(
                    UsernameCache.getLastKnownUsername(UUID.fromString(player)))));
  }
}
