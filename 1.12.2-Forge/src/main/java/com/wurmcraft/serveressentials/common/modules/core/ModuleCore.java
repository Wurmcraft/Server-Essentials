package com.wurmcraft.serveressentials.common.modules.core;

import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.config.ConfigGlobal;
import com.wurmcraft.serveressentials.common.command.DelayedCommandTicker;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.modules.core.event.PlayerDataTrackerEvent;
import com.wurmcraft.serveressentials.common.modules.core.event.RestPlayerTrackerEvent;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.RestTransferUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Core", forceAlwaysLoaded = true)
public class ModuleCore {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {
      if (hasFileData()) {
        ServerEssentials.LOG.warn(
            "File based storage detected, attempting to move over the file to the database!");
        updateToDatabase();
      }
      MinecraftForge.EVENT_BUS.register(new RestPlayerTrackerEvent());
    }
    MinecraftForge.EVENT_BUS.register(new PlayerDataTrackerEvent());
    MinecraftForge.EVENT_BUS.register(new DelayedCommandTicker());
    ModuleCore.reloadLanguageFile(((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang);
  }

  private boolean hasFileData() {
    return new File(SAVE_DIR + File.separator + "Storage" + File.separator + "account").exists();
  }

  public void reload() {}

  private static void updateToDatabase() {
    RestTransferUtils.transferToRest();
  }

  public static void reloadLanguageFile(String key) {
    SECore.dataLoader.delete(DataType.LANGUAGE, key);
    SECore.dataLoader.get(DataType.LANGUAGE, key);
  }

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigCore cfg = (ConfigCore) SECore.moduleConfigs.get("CORE");
    info.add("shutdownMessage: " + cfg.shutdownMessage);
    info.add("langStorageURL: " + cfg.langStorageURL);
    info.add("defaultLang: " + cfg.defaultLang);
    info.add("iUseAModThatMessesWithNamesPleaseFix: " + cfg.iUseAModThatMessesWithNamesPleaseFix);
    info.add("Player Cache Timeout: " + PlayerDataTrackerEvent.getPlayerCacheTimeout());
    ConfigGlobal global = ServerEssentials.config;
    info.add("general>serverID: " + global.general.serverID);
    info.add("general>debug: " + global.general.debug);
    info.add("performance>dataloaderInterval: " + global.performance.dataloaderInterval);
    info.add("performance>maxThreads: " + global.performance.maxThreads);
    info.add("performance>playerCacheTimeout: " + global.performance.playerCacheTimeout);
    info.add("performance>playerSyncInterval: " + global.performance.playerSyncInterval);
    info.add("performance>useWebsocket: " + global.performance.useWebsocket);
    info.add("storage>baseURL: " + global.storage.baseURL);
    info.add("storage>key: " + ((global.storage.key).isEmpty() ? "Empty" : "*******"));
    info.add("storage>storageType: " + global.storage.storageType);
    info.add("storage>token: " + ((global.storage.token).isEmpty() ? "Empty" : "*******"));
    return info.toArray(new String[0]);
  }
}
