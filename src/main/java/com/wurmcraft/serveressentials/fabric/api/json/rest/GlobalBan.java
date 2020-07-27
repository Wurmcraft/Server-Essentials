package com.wurmcraft.serveressentials.fabric.api.json.rest;

import com.wurmcraft.serveressentials.fabric.api.json.JsonParser;

/**
 * Used to store a ban that's persistent across all server's
 */
public class GlobalBan implements JsonParser {

  public String name;
  public String uuid;
  public String ip;
  public String reason;

  public GlobalBan(String name, String uuid, String ip) {
    this.name = name;
    this.uuid = uuid;
    this.ip = ip;
    this.reason = "";
  }

  public GlobalBan(String name, String uuid, String ip, String reason) {
    this.name = name;
    this.uuid = uuid;
    this.ip = ip;
    this.reason = reason;
  }

  @Override
  public String getID() {
    return uuid;
  }
}
