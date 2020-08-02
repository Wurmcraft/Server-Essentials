package com.wurmcraft.serveressentials.forge.server.command.json;

import com.wurmcraft.serveressentials.forge.api.json.player.Wallet;
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
    tpa.put("Default",new RankParams(new Currency("Default", 0),5,20));
    commands.put("general.tpaccept", new CommandParams(tpa));
  // Help
    Map<String, RankParams> help = new HashMap<>();
    help.put("*",new RankParams(new Currency("Default", 20),0,0));
    commands.put("general.help", new CommandParams(help));
  }

  public CommandParamsConfig(
      Map<String, CommandParams> commands) {
    this.commands = commands;
  }
}
