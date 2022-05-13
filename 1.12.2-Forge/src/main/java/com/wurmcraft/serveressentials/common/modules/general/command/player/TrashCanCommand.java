package com.wurmcraft.serveressentials.common.modules.general.command.player;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.modules.general.utils.inventory.TrashInventory;

@ModuleCommand(
    module = "General",
    name = "TrashCan",
    defaultAliases = {"Trash", "Can", "DestroyItem"})
public class TrashCanCommand {

  @Command(
      args = {},
      usage = {})
  public void trashCan(ServerPlayer player) {
    player.player.displayGUIChest(new TrashInventory(player.player, player.lang));
  }
}
