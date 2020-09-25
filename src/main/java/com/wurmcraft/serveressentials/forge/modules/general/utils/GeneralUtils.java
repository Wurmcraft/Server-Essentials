package com.wurmcraft.serveressentials.forge.modules.general.utils;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GeneralUtils {

  public static List<Object[]> requestingTPA = new ArrayList<>();

  public static int getMaxHomes(EntityPlayer player) {
    StoredPlayer playerData = PlayerUtils.get(player);
    int total = GeneralModule.config.defaultHomeCount;
    return total;
  }

  public static int getPerkLevel(EntityPlayer player, String perkName) {
    StoredPlayer playerData = PlayerUtils.get(player);
    for (String p : playerData.global.perks) {
      if (p.startsWith(perkName.toLowerCase())) {
        try {
          return Integer.parseInt(p.substring(p.lastIndexOf(".")+1));
        } catch (NoSuchElementException e) {
          return 0;
        }
      }
    }
    return 0;
  }

  public static void setPerk(EntityPlayer player, String name, int level) {
    StoredPlayer playerData = PlayerUtils.get(player);
    int currentLevel = getPerkLevel(player, name);
    List<String> currentPerks = new ArrayList<>();
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      GlobalPlayer global = RestRequestHandler.User
          .getPlayer(player.getGameProfile().getId().toString());
      Collections.addAll(currentPerks, global.perks);
    } else {
      Collections.addAll(currentPerks, playerData.global.perks);
    }
    if (currentLevel == 0) {
      currentPerks.add(name.toLowerCase() + ".amount." + level);
    } else {
      boolean found = false;
      for (int index = 0; index < currentPerks.size(); index++) {
        if (currentPerks.get(index).startsWith(name.toLowerCase())) {
          found = true;
          currentPerks.set(index, name.toLowerCase() + ".amount." + level);
        }
      }
      if (!found) {
        currentPerks.add(name.toLowerCase() + ".amount." + level);
      }
    }
    playerData.global.perks = currentPerks.toArray(new String[0]);
    SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      RestRequestHandler.User
          .overridePlayer(player.getGameProfile().getId().toString(), playerData.global);
    }
  }

  public static boolean hasActiveTPARequest(EntityPlayer player) {
    for (Object[] data : requestingTPA) {
      if (((EntityPlayer) data[1]).getGameProfile().getId()
          .equals(player.getGameProfile().getId())) {
        return true;
      }
    }
    return false;
  }

  public static Object[] getActiveTPARequest(EntityPlayer player) {
    List<Integer> timeout = new ArrayList<>();
    for (int index = 0; index < requestingTPA.size(); index++) {
      Object[] data = requestingTPA.get(index);
      long timeoutTime = (long) data[0];
      if (timeoutTime >= System.currentTimeMillis()) {
        if (((EntityPlayer) data[1]).getGameProfile().getId()
            .equals(player.getGameProfile().getId())) {
          handleTimeout(timeout);
          return new Object[]{data[1], data[2]};
        }
      } else {
        timeout.add(index);
      }
    }
    handleTimeout(timeout);
    return null;
  }

  public static void handleTimeout(List<Integer> timeout) {
    for (int x : timeout) {
      requestingTPA.remove(x);
    }
  }

  public static void updateVanish(EntityPlayer player, boolean track) {
    if (track) {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .getWorld(player.dimension)
          .getEntityTracker()
          .track(player);
    } else {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .getWorld(player.dimension)
          .getEntityTracker()
          .untrack(player);
    }
    FMLCommonHandler.instance()
        .getMinecraftServerInstance()
        .getWorld(player.dimension)
        .getEntityTracker()
        .getTrackingPlayers(player)
        .forEach(
            tp -> {
              ((EntityPlayerMP) player).connection.sendPacket(new SPacketSpawnPlayer(tp));
            });
  }

  public static String[] load(String configName) {
    File file = new File(
        SAVE_DIR + File.separator + "Misc" + File.separator + configName + ".txt");
    if (file.exists()) {
      try {
        return Files.readAllLines(file.toPath()).toArray(new String[0]);
      } catch (Exception ignored) {
      }
    }
    return new String[0];
  }

  public static String[] loadAndCreateConfig(String name) {
    String[] config = GeneralUtils.load(name);
    if (config.length == 0) {
      File file = new File(
          SAVE_DIR + File.separator + "Misc" + File.separator + name + ".txt");
      try {
        file.createNewFile();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return config;
  }

  public static void setConfig(String config, String[] lines) {
    File file = new File(
        SAVE_DIR + File.separator + "Misc" + File.separator + config + ".txt");
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      Files.write(file.toPath(), Arrays.asList(lines), StandardOpenOption.WRITE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
