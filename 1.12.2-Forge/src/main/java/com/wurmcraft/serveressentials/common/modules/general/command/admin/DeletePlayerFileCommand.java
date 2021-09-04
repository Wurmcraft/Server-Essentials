package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.UUID;

@ModuleCommand(module = "General", name = "DeletePlayerFile", defaultAliases = {"DPF"})
public class DeletePlayerFileCommand {

    @Command(args = {CommandArgument.STRING}, usage = {"player"}, canConsoleUse = true)
    public void deletePlayerFile(ServerPlayer player, String playerName) {
        String uuid = PlayerUtils.getUUIDForInput(playerName);
        if (uuid == null) {
            ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
            return;
        }
        if (PlayerUtils.isUserOnline(uuid)) {
            EntityPlayerMP otherPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(uuid));
            Language lang = CommandUtils.getPlayerLang(otherPlayer);
            otherPlayer.connection.disconnect(new TextComponentString(ChatHelper.replaceColor(lang.COMMAND_DELETEPLAYERFILE_DISCONNECT)));
        }
        File playerFile = new File(FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory(), File.separator + FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName() + File.separator + "playerdata" + File.separator + uuid + ".dat");
        if (playerFile.exists()) {
            File deletedLocation = new File(ConfigLoader.SAVE_DIR + File.separator + "Deleted" + File.separator + "Player-File" + File.separator + uuid + ".dat");
            deletedLocation.getParentFile().mkdirs();
            if (deletedLocation.exists())
                deletedLocation = new File(ConfigLoader.SAVE_DIR + File.separator + "Deleted" + File.separator + "Player-File" + File.separator + uuid + "_" + Instant.now().getEpochSecond() + ".dat");
            try {
                byte[] playerData = Files.readAllBytes(playerFile.toPath());
                Files.write(deletedLocation.toPath(), playerData, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
                if (playerFile.delete()) {
                    ChatHelper.send(player.sender, player.lang.COMMAND_DELETEPLAYERFILE.replaceAll("\\{@PLAYER@}", "" + PlayerUtils.getUsernameForInput(playerName)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
