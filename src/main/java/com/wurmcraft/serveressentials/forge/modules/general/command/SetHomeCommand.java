package com.wurmcraft.serveressentials.forge.modules.general.command;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.player.Home;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.modules.general.utils.GeneralUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "SetHome", aliases = {"SetH", "Sh"})
public class SetHomeCommand {

  private static List<String> INVALID_HOME_NAMES = new ArrayList<>();

  public SetHomeCommand() {
    INVALID_HOME_NAMES.add("list");
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"homeName"})
  public void setHome(ICommandSender sender, String name) {
    for (String invalid : INVALID_HOME_NAMES) {
      if (invalid.equalsIgnoreCase(name)) {
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SETHOME_INVALID
                .replaceAll("%NAME%", name));
        return;
      }
    }
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      StoredPlayer playerData = PlayerUtils.get(player);
      Home newHome = new Home(name, player.posX, player.posY, player.posZ,
          player.dimension);
      boolean added = false;
      if (playerData.server.homes != null && playerData.server.homes.length > 0) {
        for (int index = 0; index < playerData.server.homes.length; index++) {
          if (playerData.server.homes[index].name.equalsIgnoreCase(name)) {
            playerData.server.homes[index] = newHome;
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).GENERAL_SETHOME_UPDATED
                    .replaceAll("%HOME%", newHome.name));
            added = true;
          }
        }
        if (!added) {
          if (GeneralUtils.getMaxHomes(player) >= playerData.server.homes.length) {
            List<Home> homeList = new ArrayList<>();
            Collections.addAll(homeList, playerData.server.homes);
            homeList.add(newHome);
            playerData.server.homes = homeList.toArray(new Home[0]);
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).GENERAL_SETHOME_CREATED
                    .replaceAll("%HOME%", newHome.name));
          } else {
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).GENERAL_SETHOME_MAX
                    .replaceAll("%AMOUNT%", GeneralUtils.getMaxHomes(player) + ""));
          }
        }
      } else {
        if (GeneralUtils.getMaxHomes(player) >= playerData.server.homes.length) {
          playerData.server.homes = new Home[]{newHome};
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).GENERAL_SETHOME_CREATED
                  .replaceAll("%HOME%", newHome.name));
        } else {
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).GENERAL_SETHOME_MAX
                  .replaceAll("%AMOUNT%", GeneralUtils.getMaxHomes(player) + ""));
        }
      }
      SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
    }
  }

  @Command(inputArguments = {CommandArguments.INTEGER}, inputNames = {"homeName"})
  public void setHomeNum(ICommandSender sender, int home) {
    setHome(sender, "" + home);
  }

  @Command(inputArguments = {})
  public void empty(ICommandSender sender) {
    setHome(sender, GeneralModule.config.defaultHomeName);
  }
}
