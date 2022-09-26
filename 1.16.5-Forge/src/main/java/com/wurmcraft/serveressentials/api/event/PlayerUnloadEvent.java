package com.wurmcraft.serveressentials.api.event;

import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import net.minecraftforge.eventbus.api.Event;

public class PlayerUnloadEvent extends Event {

  public Account account;
  public LocalAccount local;

  public PlayerUnloadEvent(Account account, LocalAccount local) {
    this.account = account;
    this.local = local;
  }
}
