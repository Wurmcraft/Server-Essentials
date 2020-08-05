package com.wurmcraft.bot.json.api.json.rank;


import com.wurmcraft.bot.json.api.StoredDataType;

public class Rank implements StoredDataType {

  private String name;
  private String prefix;
  private String suffix;
  private String[] inheritance;
  private String[] permission;

  public Rank() {
    this.name = "Error";
    this.prefix = "&4[Error]";
    this.suffix = "";
    this.inheritance = new String[0];
    this.permission = new String[0];
  }

  public Rank(
      String name, String prefix, String suffix, String[] inheritance, String[] permission) {
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

  public String getName() {
    return name;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  public String[] getInheritance() {
    return inheritance;
  }

  public String[] getPermission() {
    return permission;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public void setInheritance(String[] inheritance) {
    this.inheritance = inheritance;
  }

  public void setPermission(String[] permission) {
    this.permission = permission;
  }
}
