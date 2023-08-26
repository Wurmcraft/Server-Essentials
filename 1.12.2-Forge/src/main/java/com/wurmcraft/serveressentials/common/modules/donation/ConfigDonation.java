package com.wurmcraft.serveressentials.common.modules.donation;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Donation")
public class ConfigDonation {

  public String donateURL;

  public ConfigDonation(String donateURL) {
    this.donateURL = donateURL;
  }

  public ConfigDonation() {
    this.donateURL = "https://www.curseforge.com/minecraft/mc-mods/server-essentials";
  }
}
