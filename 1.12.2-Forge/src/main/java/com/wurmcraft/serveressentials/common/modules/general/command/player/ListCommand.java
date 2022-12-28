package com.wurmcraft.serveressentials.common.modules.general.command.player;

import static com.wurmcraft.serveressentials.common.modules.chat.event.PlayerChatEvent.getRankValue;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.ServerStatus;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.chat.event.PlayerChatEvent;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator.HttpResponse;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "List", defaultAliases = {"Players", "L"})
public class ListCommand {

  @Command(args = {}, usage = {})
  public void listPlayers(ServerPlayer player) {
    ChatHelper.send(player.sender, player.lang.SPACER);
    for (EntityPlayer otPlayer : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList()
        .getPlayers()) {
      Account account = SECore.dataLoader.get(DataType.ACCOUNT,
          otPlayer.getGameProfile().getId().toString(), new Account());
      ChatHelper.send(player.sender, ChatHelper.replaceColor(getRankValue("prefix",
          PlayerChatEvent.getRanks(account))) + " " + ChatHelper.getName(otPlayer,
          account) + " (" + otPlayer.getGameProfile().getId().toString() + ")");
    }
    ChatHelper.send(player.sender, player.lang.SPACER);
  }

  @Command(args = {}, usage = {}, isSubCommand = true, subCommandAliases = {"server",
      "servers"})
  public void servers(ServerPlayer player) throws IOException {
    ChatHelper.send(player.sender, player.lang.SPACER);
    HttpResponse response = RequestGenerator.get("api/information/status");
    if (response.status == 200) {
      for (ServerStatus server : ServerEssentials.GSON.fromJson(response.response,
          ServerStatus[].class)) {
        ChatHelper.send(player.sender, ChatHelper.centerText(server.serverID));
        StringBuilder builder = new StringBuilder();
        for (String players : server.onlinePlayers) {
          builder.append(players).append(", ");
        }
      }
    }
    ChatHelper.send(player.sender, player.lang.SPACER);
  }
}
