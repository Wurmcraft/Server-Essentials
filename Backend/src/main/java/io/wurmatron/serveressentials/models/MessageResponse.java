/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.models;

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
