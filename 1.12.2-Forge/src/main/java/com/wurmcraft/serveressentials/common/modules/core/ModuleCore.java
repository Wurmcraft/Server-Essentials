package com.wurmcraft.serveressentials.common.modules.core;

import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.core.event.PlayerDataTrackerEvent;
import com.wurmcraft.serveressentials.common.modules.core.event.RestPlayerTrackerEvent;
import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Module(name = "Core", forceAlwaysLoaded = true)
public class ModuleCore {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {
      if (hasFileDate()) {
        ServerEssentials.LOG.warn(
            "File based storage detected, attempting to move over the file to the database!");
        updateToDatabase();
      }
      MinecraftForge.EVENT_BUS.register(new RestPlayerTrackerEvent());
    }
    MinecraftForge.EVENT_BUS.register(new PlayerDataTrackerEvent());
  }

  private boolean hasFileDate() {
    return new File(
        SAVE_DIR + File.separator + "Storage" + File.separator + "account").exists();
  }

  public void reload() {
  }

  private static void updateToDatabase() {
    FMLCommonHandler.instance().getMinecraftServerInstance().saveAllWorlds(true);
    FMLCommonHandler.instance().exitJava(69420,false);
    ServerEssentials.LOG.warn("Not Implemented!");
    // TODO Implement
  }
}
