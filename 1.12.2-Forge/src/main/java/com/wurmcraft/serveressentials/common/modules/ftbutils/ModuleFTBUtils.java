package com.wurmcraft.serveressentials.common.modules.ftbutils;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.modules.ftbutils.event.FTBUtilsEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

import java.io.File;


@Module(name = "FTBUtils")
public class ModuleFTBUtils {

  public static File PLAYER_RANKS = new File(
          "local" + File.separator + "ftbutilities" + File.separator + "players.txt");

  public void setup() {
    if (Loader.isModLoaded("ftbutilities")) {
      MinecraftForge.EVENT_BUS.register(new FTBUtilsEvents());
    } else {
      ServerEssentials.LOG.warn("FTBUTILS Module is disabled, FTBUtils is not found");
    }
  }

  public void reload() {}
}
