package com.wurmcraft.serveressentials.common.modules.transfer;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Transfer")
public class ConfigTransfer {

  public String transferID;
  public String[] itemBlacklist;
  public double costToTransferItem;

  public ConfigTransfer(String transferID, String[] itemBlacklist,
      double costToTransferItem) {
    this.transferID = transferID;
    this.itemBlacklist = itemBlacklist;
    this.costToTransferItem = costToTransferItem;
  }

  public ConfigTransfer() {
    this.transferID = "";
    this.itemBlacklist = new String[0];
    this.costToTransferItem = 0.0;
  }
}
