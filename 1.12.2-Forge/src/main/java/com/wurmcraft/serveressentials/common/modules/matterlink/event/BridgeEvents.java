package com.wurmcraft.serveressentials.common.modules.matterlink.event;

import static com.wurmcraft.serveressentials.common.modules.chat.event.PlayerChatEvent.getRanks;
import static com.wurmcraft.serveressentials.common.modules.matterlink.utils.MatterBridgeUtils.USER_AGENT;
import static com.wurmcraft.serveressentials.common.modules.matterlink.utils.MatterBridgeUtils.getLinkConnectURL;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.event.PlayerChatEvent;
import com.wurmcraft.serveressentials.common.modules.matterlink.utils.MatterBridgeUtils;
import com.wurmcraft.serveressentials.common.modules.matterlink.utils.json.RestMessage;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class BridgeEvents {
  public static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSSSSS'Z'");

  private boolean running = false;

  private void getAndProcessBridge() {
    if (FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getPlayerList()
            .getCurrentPlayerCount()
        > 0) {
      RestMessage[] messages = MatterBridgeUtils.getMessages();
      for (RestMessage msg : messages) {
        displayMessage(msg);
      }
    }
  }

  private void displayMessage(RestMessage msg) {
    if (!msg.event.equals("api_connected")) {
      if (isAnotherServer(msg)) {
        String rank = msg.username.substring(0, msg.username.indexOf("]"));
        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getPlayerList()
            .sendMessage(
                new TextComponentString(
                    TextFormatting.GOLD
                        + "["
                        + msg.protocol.substring(0, 1).toUpperCase()
                        + msg.protocol.substring(1).toLowerCase()
                        + "] "
                        + TextFormatting.LIGHT_PURPLE
                        + msg.username
                        + " \u00BB "
                        + TextFormatting.GRAY
                        + msg.text));
      } else {
        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getPlayerList()
            .sendMessage(
                new TextComponentString(
                    TextFormatting.GREEN
                        + "["
                        + msg.protocol.substring(0, 1).toUpperCase()
                        + msg.protocol.substring(1).toLowerCase()
                        + "] "
                        + TextFormatting.GRAY
                        + msg.username
                        + " \u00BB "
                        + msg.text));
      }
    }
  }

  private boolean isAnotherServer(RestMessage msg) {
    if (msg.username.contains("] ")) {
      return true;
    }
    return false;
  }

  @SubscribeEvent
  public void onChat(ServerChatEvent e) {
    RestMessage message =
        new RestMessage(
            "https://crafatar.com/avatars/"
                + e.getPlayer().getGameProfile().getId().toString().replaceAll("-", ""),
            "",
            MatterBridgeUtils.config.gateway,
            e.getMessage(),
            formatName(e.getPlayer()),
            "",
            ServerEssentials.config.general.serverID,
            "",
            "",
            MatterBridgeUtils.config.protocol,
            DATE_FORMAT.format(new Date()),
            e.getPlayer().getGameProfile().getId().toString(),
            null);
    if (MatterBridgeUtils.sendMessage(message) != 200) {
      ServerEssentials.LOG.warn("Failed to send message to bridge");
    }
  }

  @SubscribeEvent
  public void onLivingDeath(LivingDeathEvent e) {
    if (e.getEntityLiving() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntityLiving();
      RestMessage msg =
          new RestMessage(
              "https://crafatar.com/avatars/"
                  + player.getGameProfile().getId().toString().replaceAll("-", ""),
              "death",
              MatterBridgeUtils.config.gateway,
              e.getSource().getDeathMessage(player).getUnformattedText(),
              "",
              ServerEssentials.config.general.serverID,
              ServerEssentials.config.general.serverID,
              "",
              "",
              MatterBridgeUtils.config.protocol,
              DATE_FORMAT.format(new Date()),
              player.getGameProfile().getId().toString(),
              null);
      if (MatterBridgeUtils.sendMessage(msg) != 200) {
        ServerEssentials.LOG.warn("Failed to send message to bridge");
      }
    }
  }

  public String formatName(EntityPlayer player) {
    Account account =
        SECore.dataLoader.get(
            DataLoader.DataType.ACCOUNT, player.getGameProfile().getId().toString(), new Account());
    List<Rank> ranks = getRanks(account);
    String prefix = PlayerChatEvent.getRankValue("prefix", ranks);
    String username = ChatHelper.getName(player, account);
    String suffix = PlayerChatEvent.getRankValue("suffix", ranks);
    String color = PlayerChatEvent.getRankValue("color", ranks);
    return prefix + " " + username + suffix + " " + color;
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
    if (!running) {
      running = true;
      if (MatterBridgeUtils.config.dataCollectionType.equalsIgnoreCase("query")) {
        ServerEssentials.scheduledService.scheduleAtFixedRate(
            this::getAndProcessBridge, 0, 1, TimeUnit.SECONDS);
      } else if (MatterBridgeUtils.config.dataCollectionType.equalsIgnoreCase("stream")) {
        startHandlingStream();
      }
    }
    if (MatterBridgeUtils.config.displayLoginLogoutMessages) {
      RestMessage msg =
          new RestMessage(
              "https://crafatar.com/avatars/"
                  + e.player.getGameProfile().getId().toString().replaceAll("-", ""),
              "logout",
              MatterBridgeUtils.config.gateway,
              formatName(e.player) + " has joined the game",
              "",
              ServerEssentials.config.general.serverID,
              ServerEssentials.config.general.serverID,
              "",
              "",
              MatterBridgeUtils.config.protocol,
              DATE_FORMAT.format(new Date()),
              e.player.getGameProfile().getId().toString(),
              null);
      if (MatterBridgeUtils.sendMessage(msg) != 200) {
        ServerEssentials.LOG.warn("Failed to send message to bridge");
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
    if (MatterBridgeUtils.config.displayLoginLogoutMessages) {
      RestMessage msg =
          new RestMessage(
              "https://crafatar.com/avatars/"
                  + e.player.getGameProfile().getId().toString().replaceAll("-", ""),
              "logout",
              MatterBridgeUtils.config.gateway,
              formatName(e.player) + " has left the game",
              "",
              ServerEssentials.config.general.serverID,
              ServerEssentials.config.general.serverID,
              "",
              "",
              MatterBridgeUtils.config.protocol,
              DATE_FORMAT.format(new Date()),
              e.player.getGameProfile().getId().toString(),
              null);
      if (MatterBridgeUtils.sendMessage(msg) != 200) {
        ServerEssentials.LOG.warn("Failed to send message to bridge");
      }
    }
  }

  private void startHandlingStream() {
    Thread thread =
        new Thread(
            () -> {
              try {
                URL url = new URL(getLinkConnectURL() + "stream");
                URLConnection con = url.openConnection();
                con.setRequestProperty("User-Agent", USER_AGENT);
                if (!MatterBridgeUtils.config.token.isEmpty()) {
                  con.setRequestProperty(
                      "Authorization", "Bearer " + MatterBridgeUtils.config.token);
                }
                ServerEssentials.LOG.info("Bridge Streaming is enabled");
                BufferedReader buff =
                    new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = buff.readLine()) != null) {
                  RestMessage msg = ServerEssentials.GSON.fromJson(line, RestMessage.class);
                  displayMessage(msg);
                }
                ServerEssentials.LOG.info("Bridge Streaming is disabled");
              } catch (Exception e) {
                ServerEssentials.LOG.info("Attempting to reconnect to bridge....");
                try {
                  Thread.sleep(1000);
                  startHandlingStream();
                } catch (InterruptedException interruptedException) {
                  interruptedException.printStackTrace();
                }
              }
            },
            "Chat-Stream");
    thread.start();
  }
}
