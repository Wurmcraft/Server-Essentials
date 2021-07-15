package com.wurmcraft.serveressentials.common.modules.chat;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.event.NewChatPlayer;
import com.wurmcraft.serveressentials.common.modules.chat.event.PlayerChatEvent;
import net.minecraftforge.common.MinecraftForge;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.HashMap;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

@Module(name = "Chat", dependencies = {"Core"})
public class ModuleChat {

    public void setup() {
        try {
            // Default Channel
            String defaultChannelName = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel;
            if (SECore.dataLoader.get(DataLoader.DataType.CHANNEL, defaultChannelName) == null) {
                Channel channel = new Channel(defaultChannelName, "[" + defaultChannelName.substring(0, 1).toUpperCase() + "]",false, new HashMap<>());
                if (!SECore.dataLoader.register(DataLoader.DataType.CHANNEL, defaultChannelName, channel)) {
                    LOG.warn("Failed to create default channel '" + defaultChannelName + "'");
                }
            }
            // Load remaining channel's into memory
            SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MinecraftForge.EVENT_BUS.register(new NewChatPlayer());
        MinecraftForge.EVENT_BUS.register(new PlayerChatEvent());
    }

    public void reload() {
        NonBlockingHashMap<String, Channel> loadedChannels = SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel());
        for (String key : loadedChannels.keySet())
            SECore.dataLoader.delete(DataLoader.DataType.CHANNEL, key, true);
        // Load channels
        SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel());
    }
}
