package com.wurmcraft.serveressentials.api.models.data_wrapper;

public class ShutdownMessage {

  public String id;
  public String type;
  public String message;

  public ShutdownMessage(String id, String type, String message) {
    this.id = id;
    this.type = type;
    this.message = message;
  }
}
