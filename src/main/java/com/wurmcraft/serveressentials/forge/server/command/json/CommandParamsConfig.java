package com.wurmcraft.serveressentials.forge.server.command.json;

import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParams.RankParams;
import java.util.HashMap;
import java.util.Map;

public class CommandParamsConfig {

  public Map<String, CommandParams> commands;

  public CommandParamsConfig() {
    commands = new HashMap<>();
    // TPA
    Map<String, RankParams> tpa = new HashMap<>();
    tpa.put("Default", new RankParams(null, 5, 20));
    tpa.put("Member", new RankParams(null, 2, 10));
    commands.put("general.back", new CommandParams(tpa));
    // Help
    Map<String, RankParams> help = new HashMap<>();
    help.put("*", new RankParams(new Currency("Default", 20), 0, 0));
    commands.put("economy.pay", new CommandParams(help));
  }

  public CommandParamsConfig(
      Map<String, CommandParams> commands) {
    this.commands = commands;
  }
}
