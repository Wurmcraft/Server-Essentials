package com.wurmcraft.serveressentials.common.modules.core.command;

import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.io.File;

@ModuleCommand(
    module = "Core",
    name = "Language",
    defaultAliases = {"Lang", "L"})
public class LanguageCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = "Lang-key",
      canConsoleUse = true)
  public void changeLang(ServerPlayer player, String lang) {
    boolean validLang = false;
    Language possibleLang = null;
    try {
      possibleLang = SECore.dataLoader.get(DataType.LANGUAGE, lang, new Language());
      if (possibleLang != null) {
        validLang = true;
      }
    } catch (Exception e) {
    }
    if (!validLang) {
      ChatHelper.send(player.sender, player.lang.LANGUAGE_INVALID.replaceAll("\\{@LANG@}", lang));
      return;
    }
    if (player.sender == null) { // Console change global default lang
      ConfigCore config = ((ConfigCore) SECore.moduleConfigs.get("CORE"));
      config.defaultLang = possibleLang.langKey;
      File configFile =
          new File(SAVE_DIR + File.separator + "Modules" + File.separator + "Core.json");
      ConfigLoader.save(configFile, config);
      ChatHelper.send(player.sender, possibleLang.LANGUAGE_CHANGE.replaceAll("\\{@LANG@}", lang));
    } else {
      Account account = player.global;
      account.lang = possibleLang.langKey;
      SECore.dataLoader.update(DataType.ACCOUNT, account.uuid, account);
      ChatHelper.send(player.sender, player.lang.COMMAND_LANG);
    }
  }
}
