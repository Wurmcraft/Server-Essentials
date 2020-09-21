package com.wurmcraft.serveressentials.forge.modules.language.utils;

import com.wurmcraft.serveressentials.forge.api.json.basic.Channel;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class LanguageUtils {

  public static boolean isInChannel(Channel ch, EntityPlayer player) {
    String channel = PlayerUtils.get(player).server.channel;
    return channel.equalsIgnoreCase(ch.getID());
  }

  public static void sendMessageToChannel(Channel ch, ITextComponent msg) {
    for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      if (isInChannel(ch, player)) {
        ChatHelper.sendMessage(player, msg);
      }
    }
  }
}
