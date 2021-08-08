package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Bulletin;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;

@ModuleCommand(module = "Chat", name = "Bulletin", defaultAliases = {"OfflineAnnouncement"})
public class BulletinCommand {

    @Command(args = {CommandArgument.STRING,CommandArgument.STRING, CommandArgument.STRING}, usage = {"time", "title", "msg"})
    public void create(ServerPlayer player, String time, String title, String msg) {
        Bulletin bulletin = new Bulletin(System.currentTimeMillis() + CommandUtils.convertToTime(time), title, msg, new ArrayList<>());
        SECore.dataLoader.register(DataLoader.DataType.BULLETIN, title, bulletin);
        ChatHelper.send(player.sender, player.lang.COMMAND_BULLETIN.replaceAll("\\{@TITLE@}", title).replaceAll("\\{@TIME@}", CommandUtils.displayTime(bulletin.expiration - System.currentTimeMillis())));
        // Send to all online players
        for (EntityPlayer online : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            ChatHelper.send(online, bulletin);
            bulletin.viewedBy.add(online.getGameProfile().getId().toString());
        }
        SECore.dataLoader.update(DataLoader.DataType.BULLETIN, bulletin.title, bulletin);
    }

    @Command(args = {CommandArgument.STRING, CommandArgument.STRING, CommandArgument.STRING_ARR}, usage = {"time", "title", "msg"})
    public void create(ServerPlayer player, String time, String title, String[] msg) {
        create(player, time, title, String.join(" ", msg));
    }
}
