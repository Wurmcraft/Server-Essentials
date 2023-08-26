package com.wurmcraft.serveressentials.api.command;

import java.util.Map;

public class CommandConfig {

  // Basic
  public String name;
  public boolean enabled;
  public String permissionNode;
  public String minRank;
  public boolean secure;
  public String[] aliases;

  // Costs
  public Map<String, Long> rankCooldown;
  public Map<String, Long> rankDelay;
  public Map<String, Double> currencyCost;

  /**
   * Stores server specific command settings
   *
   * @param name the command to run in-game
   * @param enabled if this command can be used
   * @param permissionNode permission node required to use this rank
   * @param minRank minimum rank required to use this command, must also have permission
   * to run the command
   * @param secure this command can cause serious damage to the server, thus is requires a
   * "trusted" user
   * @param rankCooldown map of all rank and its designated cooldown, * for all ranks,
   * highest rank hierarchy for cooldown
   * @param rankDelay map of all rank and its designated delay's, * for all ranks, highest
   * rank hierarchy for delay
   * @param currencyCost amount of a specific currency is required to run this command
   */
  public CommandConfig(
      String name,
      boolean enabled,
      String permissionNode,
      String minRank,
      boolean secure,
      String[] aliases,
      Map<String, Long> rankCooldown,
      Map<String, Long> rankDelay,
      Map<String, Double> currencyCost) {
    this.name = name;
    this.enabled = enabled;
    this.permissionNode = permissionNode;
    this.minRank = minRank;
    this.secure = secure;
    this.aliases = aliases;
    this.rankCooldown = rankCooldown;
    this.rankDelay = rankDelay;
    this.currencyCost = currencyCost;
  }
}
