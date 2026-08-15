package com.wurmcraft.serveressentials.common.modules.chat;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.ban.ConfigBan;
import com.wurmcraft.serveressentials.common.modules.chat.event.NewChatPlayer;
import com.wurmcraft.serveressentials.common.modules.chat.event.PlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@Module(
    name = "Chat",
    dependencies = {"Core"})
public class ModuleChat {

  public void setup() {
    try {
      // Default Channel
      String defaultChannelName = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel;
      if (SECore.dataLoader.get(DataLoader.DataType.CHANNEL, defaultChannelName) == null) {
        Channel channel =
            new Channel(
                defaultChannelName,
                "[" + defaultChannelName.substring(0, 1).toUpperCase() + "]",
                false,
                new HashMap<>(),
                true,
                "",
                true,
                new String[0]);
        if (!SECore.dataLoader.register(DataLoader.DataType.CHANNEL, defaultChannelName, channel)) {
          LOG.warn("Failed to create default channel '{}'", defaultChannelName);
        }
      }
      // Load remaining channel's into memory
      SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel());
    } catch (Exception e) {
      LOG.warn(
          "Failed to create default channel '{}' ({})",
          ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel,
          e.getMessage());
    }
    MinecraftForge.EVENT_BUS.register(new NewChatPlayer());
    MinecraftForge.EVENT_BUS.register(new PlayerChatEvent());
  }

  public void reload() {
    NonBlockingHashMap<String, Channel> loadedChannels =
        SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel());
    for (String key : loadedChannels.keySet()) {
      SECore.dataLoader.delete(DataLoader.DataType.CHANNEL, key, true);
    }
    // Load channels
    SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel());
  }

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigChat cfg = (ConfigChat) SECore.moduleConfigs.get("CHAT");
    info.add("defaultChannel: " + cfg.defaultChannel);
    info.add("defaultChatFormat: " + cfg.defaultChatFormat);
    info.add("defaultMuteDuration: " + cfg.defaultMuteDuration);
    info.add("messageFormat: " + cfg.messageFormat);
    info.add("nickFormat: " + cfg.nickFormat);
    info.add("displayUUIDOnHover: " + cfg.displayUUIDOnHover);
    List<String> spyPlayers = new ArrayList<>();
    for(EntityPlayer player: ChatHelper.socialSpy)
      spyPlayers.add(player.getName());
    info.add("Social Spy: ("  + spyPlayers.size() + ")" + Strings.join(spyPlayers, ","));
    List<String> channels = new ArrayList<>();
    for(Channel ch : SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL,new Channel()).values())
     channels.add( "<" + ch.name + "@" +  ch.enabled + ">");
    info.add("Channels: (" + channels.size() + ") " + Strings.join(channels, ","));
    return info.toArray(new String[0]);
  }
}
