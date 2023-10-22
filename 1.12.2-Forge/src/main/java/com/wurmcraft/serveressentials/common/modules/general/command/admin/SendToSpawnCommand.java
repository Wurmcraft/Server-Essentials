package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "SendToSpawn")
public class SendToSpawnCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = "uuid")
  public void sendToSpawn(ServerPlayer player, String uuid) {
    for (EntityPlayerMP p :
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
      if (p.getGameProfile().getId().toString().equals(uuid)) {
        sendToSpawn((ServerPlayer) player.sender, p);
        return;
      }
    }
    File playerFile =
        new File(
            FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory(),
            File.separator
                + FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName()
                + File.separator
                + "playerdata"
                + File.separator
                + uuid
                + ".dat");
    if (playerFile.exists()) {
      try {
        NBTTagCompound playerNBT =
            CompressedStreamTools.readCompressed(new FileInputStream(playerFile));
        Account account = PlayerUtils.getLatestAccount(uuid);
        NBTTagList spawnPos = new NBTTagList();
        Location spawn = PlayerUtils.getSpawn(account.rank);
        spawnPos.appendTag(new NBTTagDouble(spawn.x));
        spawnPos.appendTag(new NBTTagDouble(spawn.y));
        spawnPos.appendTag(new NBTTagDouble(spawn.z));
        playerNBT.setTag("Pos", spawnPos);
        CompressedStreamTools.writeCompressed(playerNBT, new FileOutputStream(playerFile));
        ChatHelper.send(
            player.sender,
            player.lang.COMMAND_SENDTOSPAWN.replaceAll(
                "\\{@PLAYER@}", UsernameCache.getLastKnownUsername(UUID.fromString(uuid))));
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", uuid));
    }
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void sendToSpawn(ServerPlayer sender, EntityPlayer player) {
    TeleportUtils.teleportTo(
        (EntityPlayerMP) player,
        SECore.dataLoader.get(
            DataType.LOCAL_ACCOUNT, player.getGameProfile().getId().toString(), new LocalAccount()),
        PlayerUtils.getSpawn(
            SECore.dataLoader.get(
                    DataType.ACCOUNT, player.getGameProfile().getId().toString(), new Account())
                .rank));
    ChatHelper.send(
        sender.sender,
        sender.lang.COMMAND_SENDTOSPAWN.replaceAll("\\{@PLAYER@}", player.getDisplayNameString()));
  }
}
