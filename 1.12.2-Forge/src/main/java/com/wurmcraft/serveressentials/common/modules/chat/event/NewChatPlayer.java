package com.wurmcraft.serveressentials.common.modules.chat.event;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.event.PlayerLoadEvent;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NewChatPlayer {

    @SubscribeEvent
    public void onNewPlayer(PlayerLoadEvent e) {
        if (e.newAccount) {
            e.local.channel = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel;
            SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, e.player.getGameProfile().getId().toString(), e.local);
        }
    }
}
