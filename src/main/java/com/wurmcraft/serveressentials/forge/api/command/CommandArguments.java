package com.wurmcraft.serveressentials.forge.api.command;

public enum CommandArguments {
  PLAYER(true),
  INTEGER(false),
  DOUBLE(false),
  STRING(true),
  PERK(true),
  STRING_ARR(true),
  RANK(true),
  HOME(true),
  MODULE(true),
  CURRENCY(true),
  WARP(true);

  public boolean stringable;

  CommandArguments(boolean stringable) {
    this.stringable = stringable;
  }
}