package com.wurmcraft.serveressentials.common.modules.protect.models;

public class TrustInfo {

  public String id;
  public Action validActions;

  public TrustInfo(String id, Action validActions) {
    this.id = id;
    this.validActions = validActions;
  }

  public enum Action {
    INTERACT, BREAK, PLACE
  }
}
