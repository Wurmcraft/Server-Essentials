package com.wurmcraft.serveressentials.common.modules.matterlink;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "ChatBridge")
public class ConfigMatterlink {

  public String url;
  public String gateway;
  public String protocol;
  public String account;
  public String token;
  public boolean displayLoginLogoutMessages;
  public String dataCollectionType;
  public boolean displayServerStatus;

  public ConfigMatterlink() {
    this.url = "https://matterlink.xxxx.com:4200/api/";
    this.gateway = "chat";
    this.protocol = "mc";
    this.account = "minecraft";
    this.token = "";
    displayLoginLogoutMessages = false;
    this.dataCollectionType = "stream";
    this.displayServerStatus = false;
  }

  public ConfigMatterlink(
      String url,
      String gateway,
      String protocol,
      String account,
      String token,
      boolean displayLoginLogoutMessages,
      String dataCollectionType,
      boolean displayServerStatus) {
    this.url = url;
    this.gateway = gateway;
    this.protocol = protocol;
    this.account = account;
    this.token = token;
    this.displayLoginLogoutMessages = displayLoginLogoutMessages;
    this.dataCollectionType = dataCollectionType;
    this.displayServerStatus = displayServerStatus;
  }
}
