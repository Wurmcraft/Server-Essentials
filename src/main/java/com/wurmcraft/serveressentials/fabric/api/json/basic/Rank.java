package com.wurmcraft.serveressentials.fabric.api.json.basic;

import com.wurmcraft.serveressentials.fabric.api.json.JsonParser;

/**
 * Stored all the data about a user rank
 */
public class Rank implements JsonParser {

  public String name;
  public String prefix; // Displayed Before Username
  public String suffix; // Displayed After Username
  public String[] inheritance; // Lower Ranks / Gets Perms from
  public String[] permission;

  public Rank(String name, String prefix, String suffix, String[] inheritance,
      String[] permission) {
    this.name = name;
    this.prefix = prefix;
    this.suffix = suffix;
    this.inheritance = inheritance;
    this.permission = permission;
  }

  @Override
  public String getID() {
    return name;
  }
}
