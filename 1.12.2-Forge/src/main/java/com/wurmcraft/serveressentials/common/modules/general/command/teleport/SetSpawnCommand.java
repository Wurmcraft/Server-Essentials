package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;

@ModuleCommand(module = "General", name = "SetSpawn")
public class SetSpawnCommand {

  @Command(
      args = {},
      usage = {})
  public void setSpawn(ServerPlayer player) {
    setSpawn(player, "*");
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"rank"})
  public void setSpawn(ServerPlayer player, String rank) {
    HashMap<String, Location> spawnLocations =
        ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).spawn;
    if (spawnLocations == null) {
      spawnLocations = new HashMap<>();
    }
    spawnLocations.put(
        rank,
        new Location(
            player.player.posX,
            player.player.posY,
            player.player.posZ,
            player.player.dimension,
            player.player.rotationPitch,
            player.player.rotationYaw));
    ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).spawn = spawnLocations;
    File configFile =
        new File(SAVE_DIR + File.separator + "Modules" + File.separator + "General.json");
    try {
      Files.write(
          configFile.toPath(),
          Collections.singleton(GSON.toJson(SECore.moduleConfigs.get("GENERAL"))));
      ChatHelper.send(player.sender, player.lang.COMMAND_SETSPAWN.replaceAll("\\{@NAME@}", rank));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
