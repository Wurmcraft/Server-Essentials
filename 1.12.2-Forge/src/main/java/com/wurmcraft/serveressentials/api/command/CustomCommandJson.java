package com.wurmcraft.serveressentials.api.command;

public class CustomCommandJson {

  public String name;
  public String[] aliases;
  public CommandFunc[] functions;
  public String minRank;
  public String permissionNode;
  public boolean secure;
  public String[] rankCooldown;
  public String[] rankDelay;
  public String[] currencyCost;
  public boolean canConsoleRun;

  public CustomCommandJson(String name, String[] aliases, CommandFunc[] functions,
      String minRank, String permissionNode, boolean secure, String[] rankCooldown,
      String[] rankDelay, String[] currencyCost, boolean canConsoleRun) {
    this.name = name;
    this.aliases = aliases;
    this.functions = functions;
    this.minRank = minRank;
    this.permissionNode = permissionNode;
    this.secure = secure;
    this.rankCooldown = rankCooldown;
    this.rankDelay = rankDelay;
    this.currencyCost = currencyCost;
    this.canConsoleRun = canConsoleRun;
  }

  public static class CommandFunc {

    public CommandType type;
    public String[] values;

    public CommandFunc(CommandType type, String[] values) {
      this.type = type;
      this.values = values;
    }
  }

  public enum CommandType {
    MESSAGE, COMMAND, CONSOLE_COMMAND,
  }

}
