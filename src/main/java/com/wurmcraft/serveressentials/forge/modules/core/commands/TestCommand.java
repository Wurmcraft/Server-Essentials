package com.wurmcraft.serveressentials.forge.modules.core.commands;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;

@ModuleCommand(moduleName = "Core", name = "test")
public class TestCommand {

  @Command(inputArguments = {})
  public void test(CommandSource src) {
    src.sendFeedback(new StringTextComponent("Test Invoke Works!"), false);
  }
}
