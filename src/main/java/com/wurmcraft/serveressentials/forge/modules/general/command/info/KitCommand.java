package com.wurmcraft.serveressentials.forge.modules.general.command.info;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Kit;
import com.wurmcraft.serveressentials.forge.api.json.basic.Kit.KitInvenntory;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.StackConverter;
import java.time.Instant;
import java.util.NoSuchElementException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "General", name = "Kit")
public class KitCommand {

  @Command(inputArguments = {CommandArguments.STRING, CommandArguments.STRING,
      CommandArguments.INTEGER}, inputNames = {"create, del", "name", "timeInSeconds"})
  public void createKit(ICommandSender sender, String arg, String name, int time) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (arg.equalsIgnoreCase("create")) {
        Kit kit = new Kit(name, time, player.inventory);
        SECore.dataHandler.registerData(DataKey.KIT, kit);
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_KIT_CREATED
            .replaceAll("%KIT%", name));
      } else if (arg.equalsIgnoreCase("delete") || arg.equalsIgnoreCase("del") || arg
          .equalsIgnoreCase("remove") || arg.equalsIgnoreCase("rem")) {
        SECore.dataHandler.delData(DataKey.KIT, name, true);
      }
    }
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.STRING}, inputNames = {"delete", "name"})
  public void delKit(ICommandSender sender, String arg, String name) {
    if (arg.equalsIgnoreCase("delete") || arg.equalsIgnoreCase("del") || arg
        .equalsIgnoreCase("remove") || arg.equalsIgnoreCase("rem")) {
      SECore.dataHandler.delData(DataKey.KIT, name, true);
    }
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = "name")
  public void getKitOrList(ICommandSender sender, String name) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      if (name.equalsIgnoreCase("list")) {
        for (Kit kit : SECore.dataHandler
            .getDataFromKey(DataKey.KIT, new Kit("", 0, (KitInvenntory) null)).values()) {
          ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
          if (RankUtils.hasPermission(sender, "general.kit." + kit.name)) {
            ChatHelper.sendMessage(sender, kit.name);
          }
          ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
        }
        return;
      }
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (!RankUtils.hasPermission(sender, "general.kit." + name)) {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper
            .sendHoverMessage(sender, noPerms,
                TextFormatting.RED + "general.kit." + name);
        return;
      }
      StoredPlayer playerData = PlayerUtils.get(player);
      try {
        Kit kit = (Kit) SECore.dataHandler.getData(DataKey.KIT, name);
        if (playerData.server.kitUsage.containsKey(kit.name)
            && playerData.server.kitUsage.get(kit.name) < Instant.now()
            .getEpochSecond()) {
          if (player.inventory.isEmpty()) {
            for (int index = 0; index < player.inventory.mainInventory.size(); index++) {
              player.inventory.mainInventory
                  .set(index, StackConverter.getData(kit.invenntory.main.get(index)));
            }
            for (int index = 0; index < player.inventory.armorInventory.size(); index++) {
              player.inventory.armorInventory
                  .set(index, StackConverter.getData(kit.invenntory.armor.get(index)));
            }
          } else {
            for (String item : kit.invenntory.main) {
              if (!player.inventory
                  .addItemStackToInventory(StackConverter.getData(item))) {
                player.world.spawnEntity(
                    new EntityItem(player.world, player.posX, player.posY, player.posZ,
                        StackConverter.getData(item)));
              }
            }
            for (String item : kit.invenntory.armor) {
              if (!player.inventory
                  .addItemStackToInventory(StackConverter.getData(item))) {
                player.world.spawnEntity(
                    new EntityItem(player.world, player.posX, player.posY, player.posZ,
                        StackConverter.getData(item)));
              }
            }
          }
          playerData.server.kitUsage
              .put(kit.name, Instant.now().getEpochSecond() + kit.waitTime);
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(player).GENERAL_KIT.replaceAll("%KIT%", name));
          SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
        } else {
          ChatHelper.sendMessage(player, PlayerUtils.getLanguage(sender).COMMAND_COOLDOWN
              .replaceAll("%AMOUNT%",
                  "" + (Instant.now().getEpochSecond() - playerData.server.kitUsage
                      .get(kit.name))));
        }
      } catch (NoSuchElementException e) {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_KIT_NONE.replaceAll("%KIT%", name));
      }
    }
  }
}
