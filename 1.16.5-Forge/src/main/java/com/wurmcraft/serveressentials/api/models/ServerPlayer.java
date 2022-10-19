package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import net.minecraft.entity.player.PlayerEntity;

public class ServerPlayer {

  // TODO
//  public ICommandSender sender;
//  public EntityPlayer player;
  public LocalAccount local;
  public Account global;
  public Language lang;

  public ServerPlayer(Void sender) {
//    this.sender = sender;
//    this.player = null;
    this.local = null;
    this.global = null;
    this.lang = null;
//    this.lang =
//        SECore.dataLoader.get(
//            DataLoader.DataType.LANGUAGE,
//            ((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang,
//            new Language());
  }

  public ServerPlayer(PlayerEntity player, LocalAccount local, Account global) {
//    this.player = player;
//    this.sender = player;
    this.local = local;
    this.global = global;
    this.lang = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, global.lang, new Language());
  }
}