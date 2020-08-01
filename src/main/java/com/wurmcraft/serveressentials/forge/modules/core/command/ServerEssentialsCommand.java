package com.wurmcraft.serveressentials.forge.modules.core.command;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.Global;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.Arrays;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

@ModuleCommand(moduleName = "Core", name = "SE", aliases = {"ServerEssentials"})
public class ServerEssentialsCommand {

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {
      "modules, version, storageType"})
  public void displayInfo(ICommandSender sender, String arg) {
    if (arg.equalsIgnoreCase("modules") | arg.equalsIgnoreCase("modules") || arg
        .equalsIgnoreCase("m")) {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).CORE_SE_MODULES.replaceAll("%MODULES%",
              Arrays.toString(ModuleLoader.modules.keySet().toArray(new String[0]))));
    } else if (arg.equalsIgnoreCase("version") || arg.equalsIgnoreCase("v")) {
      for (ModContainer container : Loader.instance().getModList()) {
        if (container.getModId().equals(Global.MODID)) {
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).CORE_SE_VERSION
                  .replaceAll("%VERSION%", container.getVersion()));
          if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).CORE_SE_VERSION_REST
                    .replaceAll("%VERSION%", RestRequestHandler.validate.version));
          }
        }
      }
    } else if (arg.equalsIgnoreCase("storageType") || arg.equalsIgnoreCase("storage")
        || arg.equalsIgnoreCase("s")) {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).CORE_SE_STORAGE
          .replaceAll("%TYPE%", SECore.config.dataStorageType));
    } else {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "SE")
              .replaceAll("%ARGS%",
                  "<modules, version, storageType, reload> <moduleName>"));
    }
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.MODULE}, inputNames = {"reload", "module"})
  public void reload(ICommandSender sender, String arg, String module) {
    if (RankUtils.hasPermission(sender, "core.se.reload")) {
      if (arg.equalsIgnoreCase("reload")) {
        ModuleLoader.reloadModule(module);
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).CORE_SE_RELOAD
            .replaceAll("%MODULE%",
                module.substring(0, 1).toUpperCase() + module.substring(1)
                    .toLowerCase()));
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "SE")
                .replaceAll("%ARGS%",
                    "<modules, version, storageType, reload> <moduleName>"));
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms, TextFormatting.RED + "core.se.reload");
    }
  }
}
