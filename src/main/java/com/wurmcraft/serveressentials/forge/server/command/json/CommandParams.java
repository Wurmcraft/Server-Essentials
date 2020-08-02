package com.wurmcraft.serveressentials.forge.server.command.json;

import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import java.util.Map;

public class CommandParams {

  public Map<String, RankParams> ranks;


  public CommandParams(
      Map<String, RankParams> ranks) {
    this.ranks = ranks;
  }

  public static class RankParams {

    public Currency cost;
    public int windupTime;
    public int cooldownTime;

    public RankParams(
        Currency cost, int windupTime, int cooldownTime) {
      this.cost = cost;
      this.windupTime = windupTime;
      this.cooldownTime = cooldownTime;
    }
  }

}
