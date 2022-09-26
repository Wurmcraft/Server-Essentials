package com.wurmcraft.serveressentials.api.event;

import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class PlayerLoadEvent extends Event {

  public PlayerEntity player;
  public Account account;
  public LocalAccount local;
  public boolean newAccount;

  public PlayerLoadEvent(
      PlayerEntity player, Account account, LocalAccount local, boolean newAccount) {
    this.player = player;
    this.account = account;
    this.local = local;
    this.newAccount = newAccount;
  }
}
