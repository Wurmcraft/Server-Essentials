package com.wurmcraft.serveressentials.common.modules.ban.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.event.PlayerLoadEvent;
import com.wurmcraft.serveressentials.api.models.Ban;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BanEvents {

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void checkForBanOnLogin(PlayerLoadEvent e) {
    String ip = ((EntityPlayerMP) e.player).getPlayerIP();
    String uuid = e.player.getGameProfile().getId().toString();
    HashMap<String, String> queryParams = new HashMap<>();
    queryParams.put("uuid", uuid);
    try {
      RequestGenerator.HttpResponse response = RequestGenerator.get("api/ban/", queryParams);
      if (response.status == 200) {
        Ban[] userBans = ServerEssentials.GSON.fromJson(response.response, Ban[].class);
        for (Ban userBan : userBans) {
          if (userBan.ban_status) {
            ServerEssentials.LOG.warn(
                "User with ban has joined, ("
                    + uuid
                    + ", '"
                    + e.player.getDisplayNameString()
                    + "', Kicking");
            Language userLang = CommandUtils.getPlayerLang(e.player);
            try {
              ((EntityPlayerMP) e.player)
                  .connection.disconnect(
                      new TextComponentString(
                          ChatHelper.replaceColor(
                                  userLang.BANNED.replaceAll("\\{@REASON@}", userBan.ban_reason))
                              .replaceAll(
                                  "\\{@TIME@}",
                                  CommandUtils.displayTime(0)))); // TODO Implement Temp ban
            } catch (Exception f) {
              f.printStackTrace();
              ((EntityPlayerMP) e.player)
                  .connection.disconnect(
                      new TextComponentString(
                          "You are banned! (Unable to retrieve language '"
                              + e.account.lang
                              + "')")); // TODO Implement Temp ban
            }
          } else {
            ServerEssentials.LOG.warn(
                "User with expired ban has joined, ("
                    + uuid
                    + ", '"
                    + e.player.getDisplayNameString()
                    + "'");
            ServerEssentials.LOG.warn("Reason: " + userBan.ban_reason);
            ServerEssentials.LOG.warn("Type: " + userBan.ban_type);
          }
        }
      }
    } catch (Exception f) {
      f.printStackTrace();
    }
  }
}
