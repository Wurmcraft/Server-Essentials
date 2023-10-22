package com.wurmcraft.serveressentials.common.modules.chat.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.event.PlayerLoadEvent;
import com.wurmcraft.serveressentials.api.models.local.Bulletin;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class NewChatPlayer {

  @SubscribeEvent
  public void onNewPlayer(PlayerLoadEvent e) {
    if (e.newAccount) {
      e.local.channel = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel;
      SECore.dataLoader.update(
          DataLoader.DataType.LOCAL_ACCOUNT, e.player.getGameProfile().getId().toString(), e.local);
    }
    if (e.local.socialSpy) {
      ChatHelper.socialSpy.add(e.player);
    }
    if (SECore.dataLoader.getFromKey(DataLoader.DataType.BULLETIN, new Bulletin()) != null) {
      for (Bulletin bulletin :
          SECore.dataLoader.getFromKey(DataLoader.DataType.BULLETIN, new Bulletin()).values()) {
        if (bulletin.expiration < System.currentTimeMillis()) {
          SECore.dataLoader.delete(DataLoader.DataType.BULLETIN, bulletin.title, false);
          ServerEssentials.LOG.info(
              "Bulletin '"
                  + bulletin.title
                  + "' has expired, deleting! (viewed by "
                  + bulletin.viewedBy.size()
                  + " players)");
          continue;
        }
        if (!bulletin.viewedBy.contains(e.player.getGameProfile().getId().toString())) {
          ChatHelper.send(e.player, bulletin);
          bulletin.viewedBy.add(e.player.getGameProfile().getId().toString());
          SECore.dataLoader.update(DataLoader.DataType.BULLETIN, bulletin.title, bulletin);
        }
      }
    }
    if (RankUtils.hasPermission(e.account, "command.motd")) {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .commandManager
          .executeCommand(e.player, "motd");
    }
  }

  @SubscribeEvent
  public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
    ChatHelper.socialSpy.remove(e.player);
  }
}
