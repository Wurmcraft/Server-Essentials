package com.wurmcraft.serveressentials.common.modules.matterlink.utils.json;

public class RestMessage {

  public String avatar;
  public String event;
  public String gateway;
  public String text;
  public String username;
  public String account;
  public String channel;
  public String id;
  public String parent_id;
  public String protocol;
  public String timestamp;
  public String userid;
  public String extra;

  public RestMessage(
      String avatar,
      String event,
      String gateway,
      String text,
      String username,
      String account,
      String channel,
      String id,
      String parent_id,
      String protocol,
      String timestamp,
      String userid,
      String extra) {
    this.avatar = avatar;
    this.event = event;
    this.gateway = gateway;
    this.text = text;
    this.username = username;
    this.account = account;
    this.channel = channel;
    this.id = id;
    this.parent_id = parent_id;
    this.protocol = protocol;
    this.timestamp = timestamp;
    this.userid = userid;
    this.extra = extra;
  }

  public RestMessage() {
    this.avatar = "";
    this.event = "";
    this.gateway = "";
    this.text = "";
    this.username = "";
    this.account = "";
    this.channel = "";
    this.id = "";
    this.parent_id = "";
    this.protocol = "";
    this.timestamp = "";
    this.userid = "";
    this.extra = "";
  }
}
