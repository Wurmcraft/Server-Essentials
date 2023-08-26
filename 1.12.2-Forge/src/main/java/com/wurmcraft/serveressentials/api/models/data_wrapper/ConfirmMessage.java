package com.wurmcraft.serveressentials.api.models.data_wrapper;

public class ConfirmMessage {

  public String sender;
  public String receiver;

  public ConfirmMessage(String sender, String receiver) {
    this.sender = sender;
    this.receiver = receiver;
  }
}
