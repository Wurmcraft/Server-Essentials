package com.wurmcraft.serveressentials.forge.server.command.json;

public class CustomCommandJson {

  public String name;
  public String[] aliases;
  public CommandFunc[] functions;

  public CustomCommandJson(String name, String[] aliases,
      CommandFunc[] functions) {
    this.name = name;
    this.aliases = aliases;
    this.functions = functions;
  }

  public static class CommandFunc {

    public Command type;
    public String[] values;

    public CommandFunc(
        Command type, String[] values) {
      this.type = type;
      this.values = values;
    }
  }

  public enum Command {
    MESSAGE, COMMAND, CONSOLE_COMMAND
  }

}
