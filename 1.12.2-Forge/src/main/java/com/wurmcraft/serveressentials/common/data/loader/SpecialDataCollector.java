package com.wurmcraft.serveressentials.common.data.loader;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.event.DataRequestEvent;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.utils.URLUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

@Mod.EventBusSubscriber(modid = ServerEssentials.MODID)
public class SpecialDataCollector {

    @SubscribeEvent
    public void onLanguageRequest(DataRequestEvent e) {
        if (e.type == DataLoader.DataType.LANGUAGE) {
            try {
                String langJson = URLUtils.get(((ConfigCore) SECore.moduleConfigs.get("CORE")).langStorageURL + "/" + e.key + ".json");
                if (langJson.length() > 0)
                    e.data = GSON.fromJson(langJson, Language.class);
            } catch (Exception f) {
                f.printStackTrace();
                LOG.warn("Language '" + e.key + "' has been requested, but the server was unable to access / it does not exist!");
            }
        }
    }
}
