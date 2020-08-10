package com.wurmcraft.serveressentials.forge.modules.general.command.message;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.modules.general.utils.GeneralUtils;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "RandomMessages", aliases = {"Rm"})
public class RandomMessagesCommand {

  @Command(inputArguments = {})
  public void displayRandomMessage(ICommandSender sender) {
    String randomMessage = GeneralModule.randomMessages[sender.getEntityWorld().rand
        .nextInt(GeneralModule.randomMessages.length - 1)];
    for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      ChatHelper.sendMessage(player, randomMessage);
    }
  }

  @Command(inputArguments = {CommandArguments.STRING_ARR}, inputNames = {"lineNo", "msg"})
  public void setMsg(ICommandSender sender, String[] args) {
    if (args.length > 1) {
      if (RankUtils.hasPermission(sender, "general.randommessages.modify")) {
        try {
          List<String> randomMsg = new ArrayList<>();
          Collections.addAll(randomMsg, GeneralModule.randomMessages);
          int lineNo = Integer.parseInt(args[0]);
          if (lineNo < randomMsg.size()) {
            randomMsg.set(Integer.parseInt(args[0]),
                Strings.join(Arrays.copyOfRange(args, 1, args.length), " "));
          } else {
            randomMsg.add(Strings.join(Arrays.copyOfRange(args, 1, args.length), " "));
          }
          GeneralUtils.setConfig("randomMessages", randomMsg.toArray(new String[0]));
          ChatHelper
              .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_RANDOMMSG_SET);
          GeneralModule.randomMessages = randomMsg.toArray(new String[0]);
        } catch (NumberFormatException e) {
          ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_NUMBER
              .replaceAll("%NUM%", args[0]));
        }
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper.sendHoverMessage(sender, noPerms,
            TextFormatting.RED + "general.randommessages.modify");
      }
    } else {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE
              .replaceAll("%COMMAND%", "/RM")
              .replaceAll("%ARGS%", "<lineNo> <msg>"));
    }
  }
}
