package com.wurmcraft.serveressentials.api.models;

public class MessageResponse {

  public String title;
  public String message;

  /**
   * @param title title / summary of the message
   * @param message Message or error to provide as a response
   */
  public MessageResponse(String title, String message) {
    this.title = title;
    this.message = message;
  }
}
