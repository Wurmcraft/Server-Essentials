package com.wurmcraft.serveressentials.common.modules.general.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(module = "General", name = "TPAccept", defaultAliases = {"TpAccept"})
public class TPAAcceptCommand {

    @Command(args = {}, usage = {})
    public void acceptTPARequest(ServerPlayer player) {
        if (TPACommand.activeRequests.containsKey(player.player)) {
            EntityPlayer otherPlayer = TPACommand.activeRequests.get(player.player);
            ChatHelper.send(player.player, player.lang.COMMAND_TPACCEPT.replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
            Language otherLang = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, otherPlayer.getGameProfile().getId().toString(), new Account()).language, new Language());
            ChatHelper.send(otherPlayer, otherLang.COMMAND_TPACCEPT_OTHER.replaceAll("\\{@PLAYER@}", player.player.getDisplayNameString()));
            TeleportUtils.teleportTo((EntityPlayerMP) otherPlayer, SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, otherPlayer.getGameProfile().getId().toString(), new LocalAccount()), new Location(player.player.posX, player.player.posY, player.player.posZ, player.player.dimension, otherPlayer.rotationPitch, otherPlayer.rotationYaw));
        } else
            ChatHelper.send(player.sender, player.lang.COMMAND_TPACCEPT_NONE);
    }
}
