/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.config;

public class ServerConfig {

  public int port;
  public String host;
  public String corosOrigins;
  public long requestTimeout;
  public boolean forceLowercase;
  public boolean swaggerEnabled;
  public int cacheTime;
  public int cleanupInterval;

  public ServerConfig(int port, String host, String corosOrigins, long requestTimeout,
      boolean forceLowercase, boolean swaggerEnabled, int cacheTime,
      int cleanupInterval) {
    this.port = port;
    this.host = host;
    this.corosOrigins = corosOrigins;
    this.requestTimeout = requestTimeout;
    this.forceLowercase = forceLowercase;
    this.swaggerEnabled = swaggerEnabled;
    this.cacheTime = cacheTime;
    this.cleanupInterval = cleanupInterval;
  }

  public ServerConfig() {
    this.port = 8080;
    this.host = "localhost";
    this.corosOrigins = "";
    this.requestTimeout = 5000;
    this.forceLowercase = true;
    this.swaggerEnabled = false;
    this.cacheTime = 300;
    this.cleanupInterval = 30 * 60; // 30m in seconds
  }
}
