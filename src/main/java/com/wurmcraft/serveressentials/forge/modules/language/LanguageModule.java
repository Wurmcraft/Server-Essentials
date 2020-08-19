package com.wurmcraft.serveressentials.forge.modules.language;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Channel;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.language.event.ChatEvents;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import net.minecraftforge.common.MinecraftForge;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@Module(name = "Language", moduleDependencies = {"Rank"})
public class LanguageModule {

  public static LanguageConfig config;

  public void initSetup() {
    try {
      config = (LanguageConfig) SECore.dataHandler
          .getData(DataKey.MODULE_CONFIG, "Language");
    } catch (NoSuchElementException e) {
      File fileLoc = new File(
          SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
              + "Language.json");
      if (!fileLoc.getParentFile().exists()) {
        fileLoc.getParentFile().mkdirs();
      }
      try {
        fileLoc.createNewFile();
        Files.write(fileLoc.toPath(),
            GSON.toJson(config = new LanguageConfig()).getBytes());
      } catch (Exception f) {
        ServerEssentialsServer.LOGGER.error(
            "Failed to create default config for " + DataKey.MODULE_CONFIG + ":"
                + "Language");
      }
    }
    NonBlockingHashMap<String, Channel> channels = SECore.dataHandler.getDataFromKey(DataKey.CHANNEL, new Channel("", ""));
    if(channels.isEmpty()) {
      Channel defaultChannel = new Channel("global", "&3[G]");
      SECore.dataHandler.registerData(DataKey.CHANNEL, defaultChannel);
    }
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new ChatEvents());
  }

  public void reloadModule() {
    initSetup();
  }

}
