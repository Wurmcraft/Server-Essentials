package com.wurmcraft.serveressentials.forge.modules.language.command;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.data.Language;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.NoSuchElementException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "Language", name = "language", aliases = {"lang"})
public class LanguageCommand {

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"langKey"})
  public void changeLanguage(ICommandSender sender, String key) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      try {
        Language lang = (Language) SECore.dataHandler.getData(DataKey.LANGUAGE, key);
        StoredPlayer playerData = PlayerUtils.get(player);
        playerData.global.language = lang.key;
        if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
          GlobalPlayer data = RestRequestHandler.User
              .getPlayer(player.getGameProfile().getId().toString());
          data.language = lang.key;
          RestRequestHandler.User.overridePlayer(data.uuid, data);
        }
        ChatHelper.sendMessage(sender, lang.LANGUAGE_CHANGE);
      } catch (NoSuchElementException e) {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).LANGUAGE_INVALID.replaceAll("%LANG%", key));
      }
    }
  }
}
