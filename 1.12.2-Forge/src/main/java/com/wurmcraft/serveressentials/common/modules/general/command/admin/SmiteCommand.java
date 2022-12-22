package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "General", name = "Smite", defaultAliases = {"Lightning"})
public class SmiteCommand {

  @Command(args = {}, usage = {})
  public void smiteSelf(ServerPlayer sender) {
    sender.player.getEntityWorld().addWeatherEffect(
        new EntityLightningBolt(sender.player.getEntityWorld(), sender.player.getPosition().getX(),
            sender.player.getPosition().getY(), sender.player.getPosition().getZ(), false));
    ChatHelper.send(sender.sender, sender.lang.COMMAND_SMITE);
  }

  @Command(args = {CommandArgument.PLAYER}, usage = {"Player"})
  public void smitePlayer(ServerPlayer sender, EntityPlayer player) {
    player.getEntityWorld().addWeatherEffect(
        new EntityLightningBolt(player.getEntityWorld(), player.getPosition().getX(),
            player.getPosition().getY(), player.getPosition().getZ(), false));
    ChatHelper.send(sender.sender, sender.lang.COMMAND_SMITE_OTHER.replaceAll("\\{@PLAYER@}", ChatHelper.getName(player,
        SECore.dataLoader.get(DataType.ACCOUNT,player.getGameProfile().getId().toString(), new Account()))));
  }
}
