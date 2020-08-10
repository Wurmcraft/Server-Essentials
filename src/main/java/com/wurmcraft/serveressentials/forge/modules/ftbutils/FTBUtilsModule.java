package com.wurmcraft.serveressentials.forge.modules.ftbutils;

import com.wurmcraft.serveressentials.forge.modules.ftbutils.event.FTBUtilsEvents;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class FTBUtilsModule {

  public static File PLAYER_RANKS = new File("local" + File.separator + "ftbutilities" + File.separator + "players.txt");

  public void initSetup() {
    if (!Loader.isModLoaded("ftbutilities")) {
      ServerEssentialsServer.LOGGER.warn("Unable to load FTBUtils Module, Missing FTBUtils!");
    } else {
      MinecraftForge.EVENT_BUS.register(new FTBUtilsEvents());
    }
  }

  public void finalizeModule() {

  }
}
