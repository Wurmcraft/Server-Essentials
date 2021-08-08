package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

@ModuleCommand(module = "Chat", name = "Motd")
public class MotdCommand {

    @Command(args = {}, usage = {})
    public void displayMotd(ServerPlayer player) {
        String[] motd = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).motd;
        for (String line : motd)
            ChatHelper.send(player.sender, line.replaceAll("%NAME%", ChatHelper.getName(player.player, player.global)));
    }

    @Command(args = {CommandArgument.STRING_ARR}, usage = {"motd"})
    public void setMotd(ServerPlayer player, String[] newMotd) {
        if (RankUtils.hasPermission(player.global, "command.motd.set")) {
            String[] motd = String.join(" ", newMotd).split(";");
            ((ConfigChat) SECore.moduleConfigs.get("CHAT")).motd = motd;
            File configFile = new File(SAVE_DIR + File.separator + "Modules" + File.separator + "Chat.json");
            try {
                Files.write(configFile.toPath(), Collections.singleton(GSON.toJson(SECore.moduleConfigs.get("CHAT"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatHelper.send(player.sender, player.lang.COMMAND_MOTD);
        } else
            ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
}
