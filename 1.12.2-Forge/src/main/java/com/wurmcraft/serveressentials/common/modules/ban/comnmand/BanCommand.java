package com.wurmcraft.serveressentials.common.modules.ban.comnmand;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Ban;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;

// TODO Disable user market entries, etc...
@ModuleCommand(module = "Ban", name = "ban", defaultAliases = {"GlobalBan"})
public class BanCommand {

  @Command(
      args = {CommandArgument.STRING, CommandArgument.STRING, CommandArgument.STRING_ARR},
      usage = {"player uuid or name", "time", "reason"},
      isSubCommand = true,
      subCommandAliases = {"add", "set"},
      canConsoleUse = true
  )
  public void create(ServerPlayer sender, String player, String time, String[] reason) {
    String uuid = PlayerUtils.getUUIDForInput(player);
    Ban ban = new Ban(-1, uuid, "", "", getName(sender), "In-Game",
        String.join(" ", reason),
        "" + Instant.now().getEpochSecond(), "Temp",
        "{\"time\": " + CommandUtils.convertToTime(time) + "}", true);
    try {
      HttpResponse response = RequestGenerator.post("api/ban",
          ServerEssentials.GSON.toJson(ban));
      if (response.status == 201) {
        ChatHelper.send(sender.sender,
            sender.lang.COMMAND_BAN_CREATE.replaceAll("\\{@USER@}", uuid)
                .replaceAll("\\{@TIME@}",
                    CommandUtils.displayTime(CommandUtils.convertToTime(time))));
        ServerEssentials.LOG.info(
            "User '" + uuid + "' has been banned by " + ban.banned_by + " for '"
                + ban.ban_reason + "'");
      } else {
        ServerEssentials.LOG.warn(
            "Failed to ban user '" + uuid + "' (" + response.status + ")");
        ServerEssentials.LOG.warn(response.response);
      }
    } catch (Exception e) {
      ServerEssentials.LOG.warn(
          "Failed to ban user '" + uuid + "' (" + e.getMessage() + ")");
    }
  }

  private static String getName(ServerPlayer player) {
    if (player.sender instanceof EntityPlayer) {
      return ChatHelper.getName(player.player, player.global);
    } else {
      return "Console-" + ServerEssentials.config.general.serverID;
    }
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.STRING, CommandArgument.STRING_ARR},
      usage = {"player uuid or name", "time", "reason"},
      isSubCommand = true,
      subCommandAliases = {"add", "set"},
      canConsoleUse = true
  )
  public void create(ServerPlayer sender, String player, String[] reason) {
    create(sender, player, "36160d", reason);  // 99 years ban
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"player uuid or name"},
      isSubCommand = true,
      subCommandAliases = {"del", "remove", "rem", "d", "r", "destroy"},
      canConsoleUse = true
  )
  public void delete(ServerPlayer sender, String player) {
    String uuid = PlayerUtils.getUUIDForInput(player);
    try {
      HashMap<String, String> queryParams = new HashMap<>();
      queryParams.put("uuid", uuid);
      HttpResponse response = RequestGenerator.get("api/ban");
      if (response.status == 200) {
        Ban[] bans = ServerEssentials.GSON.fromJson(response.response, Ban[].class);
        for (Ban ban : bans) {
          if (ban.ban_status) {
            ban.ban_status = false;
            RequestGenerator.put("api/ban/" + ban.ban_id,
                ServerEssentials.GSON.toJson(ban));
          }
        }
      }
      ChatHelper.send(sender.sender,
          sender.lang.COMMAND_BAN_DELETE.replaceAll("\\{@USER@}", uuid));
    } catch (Exception e) {
      ServerEssentials.LOG.warn(
          "Failed to ban user '" + uuid + "' (" + e.getMessage() + ")");
    }
  }


  @Command(
      args = {CommandArgument.STRING},
      usage = {"player uuid or name"},
      isSubCommand = true,
      subCommandAliases = {"check", "c", "look", "lk", "l"},
      canConsoleUse = true
  )
  public void lookup(ServerPlayer sender, String player) {
    String uuid = PlayerUtils.getUUIDForInput(player);
    try {
      HashMap<String, String> queryParams = new HashMap<>();
      queryParams.put("uuid", uuid);
      HttpResponse response = RequestGenerator.get("api/ban");
      if (response.status == 200) {
        Ban[] bans = ServerEssentials.GSON.fromJson(response.response, Ban[].class);
        for (Ban ban : bans) {
          ChatHelper.send(sender.sender, sender.lang.SPACER);
          ChatHelper.send(sender.sender,
              sender.lang.COMMAND_BAN_LOOKUP_ID.replaceAll("\\{@ID@}", "" + ban.ban_id));
          ChatHelper.send(sender.sender,
              sender.lang.COMMAND_BAN_LOOKUP_REASON.replaceAll("\\{@REASON@}",
                  "" + ban.ban_reason));
          ChatHelper.send(sender.sender,
              sender.lang.COMMAND_BAN_LOOKUP_TIME.replaceAll("\\{@TIME@}",
                  CommandUtils.displayTime(Long.parseLong(ban.timestamp))));
        }
        ChatHelper.send(sender.sender, sender.lang.SPACER);
      }
      ChatHelper.send(sender.sender,
          sender.lang.COMMAND_BAN_DELETE.replaceAll("\\{@USER@}", uuid));
    } catch (Exception e) {
      ServerEssentials.LOG.warn(
          "Failed to ban user '" + uuid + "' (" + e.getMessage() + ")");
    }
  }
}
