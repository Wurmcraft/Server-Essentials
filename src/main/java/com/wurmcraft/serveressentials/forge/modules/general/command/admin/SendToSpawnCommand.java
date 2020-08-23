package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "sendtospawn")
public class SendToSpawnCommand {

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = "uuid")
  public void sendToSpawn(ICommandSender sender, String uuid) {
    for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      if (player.getGameProfile().getId().toString().equals(uuid)) {
        sendToSpawn(sender, player);
        return;
      }
    }
    File playerFile = new File(
        FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory(),
        File.separator + FMLCommonHandler.instance().getMinecraftServerInstance()
            .getFolderName() + File.separator + "playerdata" + File.separator + uuid
            + ".dat");
    if (playerFile.exists()) {
      try {
        NBTTagCompound playerNBT = CompressedStreamTools
            .readCompressed(new FileInputStream(playerFile));
        NBTTagList spawnPos = new NBTTagList();
        LocationWrapper spawn = getSpawn();
        spawnPos.appendTag(new NBTTagDouble(spawn.x));
        spawnPos.appendTag(new NBTTagDouble(spawn.y));
        spawnPos.appendTag(new NBTTagDouble(spawn.z));
        playerNBT.setTag("Pos", spawnPos);
        CompressedStreamTools
            .writeCompressed(playerNBT, new FileOutputStream(playerFile));
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_SENDTOSPAWN.replaceAll("%PLAYER%",
                UsernameCache.getLastKnownUsername(UUID.fromString(uuid))));
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_PLAYER_NONE
          .replaceAll("%PLAYER%", uuid));
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"player"})
  public void sendToSpawn(ICommandSender sender, EntityPlayer player) {
    TeleportUtils.teleportTo(player, getSpawn());
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SENDTOSPAWN
        .replaceAll("%PLAYER%", player.getDisplayNameString()));
  }

  private static LocationWrapper getSpawn() {
    if (ModuleLoader.getLoadedModule("General") != null) {
      if (GeneralModule.config.spawn.spawns.get("spawn") != null) {
        return GeneralModule.config.spawn.spawns.get("spawn");
      } else if (GeneralModule.config.spawn.spawns.get("firstJoin") != null) {
        return GeneralModule.config.spawn.spawns.get("firstJoin");
      }
    }
    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    WorldServer overworld = server.getWorld(0);
    return new LocationWrapper(overworld.getSpawnPoint().getX(),
        overworld.getSpawnPoint().getY() + 2, overworld.getSpawnPoint().getZ(), 0);
  }
}
