package io.wurmatron.serveressentials.models.data_wrapper;

public class ChatMessage {

    public String serverType;
    public String serverID;
    public String senderName;
    public String message;
    public String channel;

    public ChatMessage(String serverType, String serverID, String senderName, String message, String channel) {
        this.serverType = serverType;
        this.serverID = serverID;
        this.senderName = senderName;
        this.message = message;
        this.channel = channel;
    }
}
