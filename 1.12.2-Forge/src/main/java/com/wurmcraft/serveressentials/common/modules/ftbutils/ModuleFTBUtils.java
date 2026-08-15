package com.wurmcraft.serveressentials.common.modules.ftbutils;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.modules.economy.MarketHelper;
import com.wurmcraft.serveressentials.common.modules.ftbutils.event.FTBUtilsEvents;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import joptsimple.internal.Strings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

@Module(name = "FTBUtils")
public class ModuleFTBUtils {

  public static File PLAYER_RANKS =
      new File("local" + File.separator + "ftbutilities" + File.separator + "players.txt");

  public void setup() {
    if (Loader.isModLoaded("ftbutilities")) {
      MinecraftForge.EVENT_BUS.register(new FTBUtilsEvents());
    } else {
      ServerEssentials.LOG.warn("FTBUTILS Module is disabled, FTBUtils is not found");
    }
  }

  public void reload() {}

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    return info.toArray(new String[0]);
  }
}
