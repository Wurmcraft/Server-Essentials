package com.wurmcraft.serveressentials.common.utils;

import net.minecraft.util.ResourceLocation;

public class DataWrapper {

  public String modid;
  public String name;

  public DataWrapper(String modid, String name) {
    this.modid = modid;
    this.name = name;
  }

  public ResourceLocation toResourceLocation() {
    return new ResourceLocation(modid, name);
  }
}
