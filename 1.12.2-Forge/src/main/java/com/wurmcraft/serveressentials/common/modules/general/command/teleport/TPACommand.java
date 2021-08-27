package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@ModuleCommand(module = "General", name = "Tpa", defaultCooldown = {"*:5s"}, defaultDelay = {"*:2s"})
public class TPACommand {

    public static NonBlockingHashMap<EntityPlayer, EntityPlayer> activeRequests = new NonBlockingHashMap<>();

    @Command(args = {CommandArgument.PLAYER}, usage = "player")
    public void tpPlayer(ServerPlayer player, EntityPlayerMP otherPlayer) {
        ChatHelper.send(player.player, player.lang.COMMAND_TPA.replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
        if (!ChatHelper.isIgnored(SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, otherPlayer.getGameProfile().getId().toString(), new LocalAccount()), player.player.getGameProfile().getId().toString())) {
            activeRequests.put(otherPlayer, player.player);
            Language otherLang = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, ((Account) SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, otherPlayer.getGameProfile().getId().toString())).language, new Language());
            ChatHelper.send(otherPlayer, otherLang.COMMAND_TPA_OTHER.replaceAll("\\{@PLAYER@}", player.player.getDisplayNameString()));
        }
    }
}
