package com.wurmcraft.serveressentials.common.modules.general.command.utils;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator.HttpResponse;
import java.util.UUID;
import net.minecraftforge.common.UsernameCache;

@ModuleCommand(
    module = "General",
    name = "Uuid",
    defaultAliases = {"Id", "LookupUuid"})
public class UUIDCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = {"name"},
      canConsoleUse = true)
  public void lookupUUID(ServerPlayer player, String username) {
    boolean basic = UsernameCache.getMap().containsValue(username);
    if (basic) {
      for (UUID uuid : UsernameCache.getMap().keySet()) {
        if (UsernameCache.getLastKnownUsername(uuid).equalsIgnoreCase(username)) {
          ChatHelper.send(
              player.sender,
              player.lang.COMMAND_WHOIS_UUID.replaceAll("\\{@UUID@}", uuid.toString()));
          return;
        }
      }
    }
    // Rest API Lookup
    if (SECore.dataLoader instanceof RestDataLoader) {
      try {
        HttpResponse response = RequestGenerator.get("api/lookup/uuid/" + username);
        if (response.status == 200) {
          Account uuid = ServerEssentials.GSON.fromJson(response.response, Account.class);
          ChatHelper.send(
              player.sender, player.lang.COMMAND_WHOIS_UUID.replaceAll("\\{@UUID@}", uuid.uuid));
        } else {
          ChatHelper.send(
              player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", username));
        }
      } catch (Exception e) {
        ServerEssentials.LOG.debug(e.getMessage());
        ChatHelper.send(
            player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", username));
      }
    } else {
      ChatHelper.send(
          player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", username));
    }
  }
}
