package com.wurmcraft.serveressentials.forge.modules.ftbutils.utils;

import static com.feed_the_beast.ftbutilities.ranks.Ranks.INSTANCE;

import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.ftbutils.FTBUtilsModule;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class FtbUtilsUtils {

  public static int getMaxClaims(StoredPlayer player, Rank rank) {
    int total = 0;
    if (rank != null && rank.permission != null) {
      for (String p : rank.permission) {
        if (p.startsWith("ftbutils.claim.")) {
          try {
            total += Integer.parseInt(p.substring(p.lastIndexOf(".") + 1));
          } catch (NumberFormatException ignored) {
          }
        }
      }
      if (player.global.perks != null && player.global.perks.length > 0) {
        for (String p : player.global.perks) {
          if (p.startsWith("claimblocks.amount.")) {
            try {
              total += Integer.parseInt(p.substring(p.lastIndexOf(".") + 1));
            } catch (NumberFormatException ignored) {
            }
          }
        }
      }
    }
    return total;
  }

  public static int getMaxChunkLoading(StoredPlayer player, Rank rank) {
    int total = 0;
    if (rank != null && rank.permission != null) {
      for (String p : rank.permission) {
        if (p.startsWith("ftbutils.chunkloading.")) {
          try {
            total += Integer.parseInt(p.substring(p.lastIndexOf(".") + 1));
          } catch (NumberFormatException ignored) {
          }
        }
      }
      if (player.global.perks != null && player.global.perks.length > 0) {
        for (String p : player.global.perks) {
          if (p.startsWith("chunkloading.amount.")) {
            try {
              total +=Integer.parseInt(p.substring(p.lastIndexOf(".") + 1));
            } catch (NumberFormatException ignored) {
            }
          }
        }
      }
    }
    return total;
  }

  public static void setPlayerClaimBlocks(String uuid, int claimAmount,
      int loadingAmount) {
    try {
      FileWriter writer = new FileWriter(FTBUtilsModule.PLAYER_RANKS, true);
      writer.append("[" + uuid + "]" + "\n"
          + "ftbutilities.claims.max_chunks: " + claimAmount + "\n"
          + "ftbutilities.chunkloader.max_chunks: " + loadingAmount + "\n");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    INSTANCE.reload();
  }

  public static void updatePlayerClaimBlocks(EntityPlayer player, int claimAmount,
      int loadingAmount) {
    deletePlayerClaimBlocks(player);
    setPlayerClaimBlocks(player.getGameProfile().getId().toString(), claimAmount,
        loadingAmount);
  }

  private static void deletePlayerClaimBlocks(EntityPlayer player) {
    try {
      List<String> fileData = Files.readAllLines(FTBUtilsModule.PLAYER_RANKS.toPath());
      for (int i = 0; i < fileData.size(); i++) {
        if (fileData.get(i)
            .startsWith("[" + player.getGameProfile().getId().toString())) {
          fileData.remove(i);
          fileData.remove(i);
          fileData.remove(i);
          i += 2;
        }
      }
      Files.write(FTBUtilsModule.PLAYER_RANKS.toPath(), fileData,
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
