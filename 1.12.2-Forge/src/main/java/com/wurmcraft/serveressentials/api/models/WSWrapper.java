/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models;

public class WSWrapper {

  public int status;
  public Type type;
  public DataWrapper data;

  /**
   * Wrapper for use by the web sockets between the servers and api
   *
   * @param status http status code for this message
   * @param type type of message
   * @param data instance of the data with its type included
   * @see DataWrapper
   */
  public WSWrapper(int status, Type type, DataWrapper data) {
    this.status = status;
    this.type = type;
    this.data = data;
  }

  public enum Type {
    MESSAGE,
    REQUEST_RESPONSE,
    UPDATE,
    DELETE,
    ACTION
  }
}
