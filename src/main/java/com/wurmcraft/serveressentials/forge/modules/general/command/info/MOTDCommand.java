package com.wurmcraft.serveressentials.forge.modules.general.command.info;

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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "General", name = "Motd")
public class MOTDCommand {

  @Command(inputArguments = {})
  public void displayMOTD(ICommandSender sender) {
    for (String msg : GeneralModule.motd) {
      ChatHelper.sendMessage(sender, msg.replaceAll("%PLAYER%", sender.getDisplayName().getFormattedText()));
    }
  }

  @Command(inputArguments = {CommandArguments.STRING_ARR}, inputNames = {"lineNo", "msg"})
  public void setMotd(ICommandSender sender, String[] args) {
    if (args.length > 1) {
      if (RankUtils.hasPermission(sender, "general.motd.modify")) {
        try {
          List<String> motd = new ArrayList<>();
          Collections.addAll(motd, GeneralModule.motd);
          int lineNo = Integer.parseInt(args[0]);
          if (lineNo < motd.size()) {
            motd.set(Integer.parseInt(args[0]),
                Strings.join(Arrays.copyOfRange(args, 1, args.length), " "));
          } else {
            motd.add(Strings.join(Arrays.copyOfRange(args, 1, args.length), " "));
          }
          GeneralUtils.setConfig("motd", motd.toArray(new String[0]));
          ChatHelper
              .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_MOTD_SET);
          GeneralModule.motd = motd.toArray(new String[0]);
        } catch (NumberFormatException e) {
          ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_NUMBER
              .replaceAll("%NUM%", args[0]));
        }
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper.sendHoverMessage(sender, noPerms,
            TextFormatting.RED + "general.motd.modify");
      }
    } else {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "/motd")
              .replaceAll("%ARGS%", "<lineNo> <msg>"));
    }
  }
}
