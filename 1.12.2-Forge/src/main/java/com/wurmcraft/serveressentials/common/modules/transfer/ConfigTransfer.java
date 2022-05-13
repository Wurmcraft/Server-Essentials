package com.wurmcraft.serveressentials.common.modules.transfer;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Transfer")
public class ConfigTransfer {

  public String transferID;
  public String[] itemBlacklist;

  public ConfigTransfer(String transferID, String[] itemBlacklist) {
    this.transferID = transferID;
    this.itemBlacklist = itemBlacklist;
  }

  public ConfigTransfer() {
    this.transferID = "";
    this.itemBlacklist = new String[0];
  }
}
