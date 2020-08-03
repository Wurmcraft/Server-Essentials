package com.wurmcraft.serveressentials.forge.server.json;

public class MOTDSettings {

  public String[] onlineMOTD;
  public int onlineMOTDswapTime;

  public MOTDSettings() {
    this.onlineMOTD = new String[]{"&cServer is running Server Essentials",
        "&bServer Essentials is a mod by Wurmatron"};
    this.onlineMOTDswapTime = 5;
  }

  public MOTDSettings(String[] onlineMOTD, int onlineMOTDswapTime) {
    this.onlineMOTD = onlineMOTD;
    this.onlineMOTDswapTime = onlineMOTDswapTime;
  }
}
