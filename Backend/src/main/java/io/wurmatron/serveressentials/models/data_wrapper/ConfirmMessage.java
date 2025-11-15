/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2025 Wurmcraft
 */
package io.wurmatron.serveressentials.models.data_wrapper;

public class ConfirmMessage {

  public String sender;
  public String receiver;

  public ConfirmMessage(String sender, String receiver) {
    this.sender = sender;
    this.receiver = receiver;
  }
}
