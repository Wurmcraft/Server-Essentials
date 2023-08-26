package com.wurmcraft.serveressentials.api.models.local;

import java.util.List;

public class Bulletin {

  public long expiration;
  public String title;
  public String message;
  public List<String> viewedBy;

  public Bulletin(long expiration, String title, String message, List<String> viewedBy) {
    this.expiration = expiration;
    this.title = title;
    this.message = message;
    this.viewedBy = viewedBy;
  }

  public Bulletin() {
  }
}
