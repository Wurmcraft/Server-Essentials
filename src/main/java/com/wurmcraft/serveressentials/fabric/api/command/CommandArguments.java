package com.wurmcraft.serveressentials.fabric.api.command;

public enum CommandArguments {
  PLAYER(true),
  INTEGER(false),
  DOUBLE(false),
  STRING(true),
  PERK(true),
  STRING_ARR(true),
  RANK(true),
  HOME(true);

  public boolean stringable;

  CommandArguments(boolean stringable) {
    this.stringable = stringable;
  }
}