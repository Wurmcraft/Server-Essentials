package com.wurmcraft.serveressentials.common.modules.chat.event;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.event.PlayerLoadEvent;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class NewChatPlayer {

    @SubscribeEvent
    public void onNewPlayer(PlayerLoadEvent e) {
        if (e.newAccount) {
            e.local.channel = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel;
            SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, e.player.getGameProfile().getId().toString(), e.local);
        }
        if (e.local.socialSpy)
            ChatHelper.socialSpy.add(e.player);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        ChatHelper.socialSpy.remove(e.player);
    }
}
