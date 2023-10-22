/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models;

public class Event {

  public String event;
  public String type;
  public String jsonData;

  public Event(String event, String type, String jsonData) {
    this.event = event;
    this.type = type;
    this.jsonData = jsonData;
  }
}
