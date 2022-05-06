package io.wurmatron.serveressentials.models;

public class DMMessage {

  public String senderUUID;
  public String displayName;
  public String locationID;
  public String message;
  public String receiverID;

  public DMMessage(
      String senderUUID, String displayName, String locationID, String message, String receiverID) {
    this.senderUUID = senderUUID;
    this.displayName = displayName;
    this.locationID = locationID;
    this.message = message;
    this.receiverID = receiverID;
  }
}
