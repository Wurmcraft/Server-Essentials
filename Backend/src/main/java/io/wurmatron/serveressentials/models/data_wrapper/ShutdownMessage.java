/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2025 Wurmcraft
 */
package io.wurmatron.serveressentials.models.data_wrapper;

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
