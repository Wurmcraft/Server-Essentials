package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.player.Home;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import java.util.*;

@ModuleCommand(moduleName = "General", name = "DelHome", aliases = {"DeleteHome", "DHome",
    "RemoveHome", "RemHome", "RHome"})
public class DelHomeCommand {

  @Command(inputArguments = {CommandArguments.HOME}, inputNames = {"homeName"})
  public void delHome(ICommandSender sender, Home home) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      StoredPlayer playerData = PlayerUtils.get(player);
      for (int index = 0; index < playerData.server.homes.length; index++) {
        if (playerData.server.homes[index].name.equals(home.name)) {
          List<Home> currentHomes = new ArrayList<>();
          Collections.addAll(currentHomes, playerData.server.homes);
          currentHomes.remove(playerData.server.homes[index]);
          playerData.server.homes = currentHomes.toArray(new Home[0]);
          SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).GENERAL_DELHOME_HOME
                  .replaceAll("%NAME%", home.name));
        }
      }
    }
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"all"})
  public void delHome(ICommandSender sender, String arg) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (arg.equalsIgnoreCase("all")) {
        StoredPlayer playerData = PlayerUtils.get(player);
        playerData.server.homes = new Home[0];
        SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_DELHOME_ALL);
      } else {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_USAGE
            .replaceAll("%COMMAND%", "/DelHome").replaceAll("%ARGS%", "<homeName>"));
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_USAGE
            .replaceAll("%COMMAND%", "/DelHome").replaceAll("%ARGS%", "<all>"));
      }
    }
  }

  public void delHome(ICommandSender sender, int home) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      StoredPlayer playerData = PlayerUtils.get(player);
      boolean found = false;
      for (Home h : playerData.server.homes) {
        if (h.name.equalsIgnoreCase(home + "")) {
          delHome(sender, h);
          found = true;
        }
      }
      if (!found) {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_USAGE
            .replaceAll("%COMMAND%", "/DelHome").replaceAll("%ARGS%", "<homeName>"));
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_USAGE
            .replaceAll("%COMMAND%", "/DelHome").replaceAll("%ARGS%", "<all>"));
      }
    }
  }

  @Command(inputArguments = {})
  public void delDefault(ICommandSender sender) {
    delHome(sender, GeneralModule.config.defaultHomeName);
  }
}
