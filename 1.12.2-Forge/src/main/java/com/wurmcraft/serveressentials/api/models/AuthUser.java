/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models;

public class AuthUser {

  public String type;
  public String name;
  public String[] perms;
  public String token;
  public String key;
  public long expiration;

  public AuthUser(
      String type, String name, String[] perms, String token, String key,
      long expiration) {
    this.type = type;
    this.name = name;
    this.perms = perms;
    this.token = token;
    this.key = key;
    this.expiration = expiration;
  }
}
