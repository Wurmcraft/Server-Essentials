package com.wurmcraft.serveressentials.fabric.api.json.rest;

import com.wurmcraft.serveressentials. fabric.api.json.JsonParser;

/**
 * Used by SE to validate if it has a connection to send get/post/put request to the database
 */
public class RestValidate implements JsonParser {

  public String version;

  public RestValidate(String version) {
    this.version = version;
  }

  @Override
  public String getID() {
    return "rest";
  }
}
