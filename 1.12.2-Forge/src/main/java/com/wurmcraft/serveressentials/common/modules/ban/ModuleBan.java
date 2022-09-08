package com.wurmcraft.serveressentials.common.modules.ban;


import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.ban.event.BanEvents;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Ban")
public class ModuleBan {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {
      if (((ConfigBan) SECore.moduleConfigs.get("BAN")).followRestBans) {
        MinecraftForge.EVENT_BUS.register(new BanEvents());
      } else
        ServerEssentials.LOG.info("Not following global bans, followRestBans is false");
    } else {
      ServerEssentials.LOG.warn("Module 'Ban' does not work in 'File' Storage mode!");
      SECore.modules.remove("BAN");
    }
  }

  public void reload() {

  }

}
