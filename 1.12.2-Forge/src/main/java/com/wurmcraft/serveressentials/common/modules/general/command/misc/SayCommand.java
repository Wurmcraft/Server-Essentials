package com.wurmcraft.serveressentials.common.modules.general.command.misc;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import joptsimple.internal.Strings;

@ModuleCommand(module = "General", name = "Say")
public class SayCommand {

  @Command(
      args = {CommandArgument.STRING_ARR},
      usage = {"MSG"},
      canConsoleUse = true)
  public void sayCommand(ServerPlayer sender, String[] msg) {
    ChatHelper.sendToAll("&6[Server] &b" + Strings.join(msg, " "));
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"MSG"},
      canConsoleUse = true)
  public void sayCommand(ServerPlayer sender, String msg) {
    sayCommand(sender, new String[] {msg});
  }
}
