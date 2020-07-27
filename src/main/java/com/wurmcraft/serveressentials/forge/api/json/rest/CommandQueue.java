package com.wurmcraft.serveressentials.forge.api.json.rest;

/**
 * Queue up a command from another server or entirely outside the server network
 */
public class CommandQueue {

  public RequestedCommand[] commands;

  public static class RequestedCommand {

    public String serverID;
    public String command;


    public RequestedCommand(String serverID, String command) {
      this.serverID = serverID;
      this.command = command;
    }
  }

}
