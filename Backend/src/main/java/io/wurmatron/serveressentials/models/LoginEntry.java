/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.models;

public class LoginEntry {

  public String type;
  public String id;
  public String auth;
  public String validation;

  public LoginEntry(String type, String id, String auth, String validation) {
    this.type = type;
    this.id = id;
    this.auth = auth;
    this.validation = validation;
  }
}
