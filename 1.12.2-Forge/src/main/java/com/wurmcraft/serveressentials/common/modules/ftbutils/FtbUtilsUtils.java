package com.wurmcraft.serveressentials.common.modules.ftbutils;

import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FtbUtilsUtils {

    public static int get(Account player, Rank[] ranks, String[] checks) {
        int total = 0;
        if (ranks != null) {
            int largest = 0;
            for (Rank r : ranks) {
                String[] perms = RankUtils.permList(r.name);
                for (String p : perms) {
                    if (p.startsWith("ftbutils.claim.")) {
                        try {
                            int x = Integer.parseInt(p.substring(p.lastIndexOf(".") + 1));
                            if (x > largest)
                                largest = x;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
            total = total + largest;
        }
        if (player.perks != null) {
            for (String p : player.perks) {
                if (p.startsWith("claimblocks.amount.")) {
                    try {
                        total += Integer.parseInt(p.substring(p.lastIndexOf(".") + 1));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return total;
    }

    public static void setPlayerClaimBlocks(String uuid, int claimAmount,
                                            int loadingAmount) {
        try {
            FileWriter writer = new FileWriter(ModuleFTBUtils.PLAYER_RANKS, true);
            writer.append("[" + uuid + "]" + "\n"
                    + "ftbutilities.claims.max_chunks: " + claimAmount + "\n"
                    + "ftbutilities.chunkloader.max_chunks: " + loadingAmount + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        INSTANCE.reload(); // TODO Add FTB Utils as lib and reload when updating
    }

    public static void updatePlayerClaimBlocks(EntityPlayer player, int claimAmount,
                                               int loadingAmount) {
        deletePlayerClaimBlocks(player);
        setPlayerClaimBlocks(player.getGameProfile().getId().toString(), claimAmount,
                loadingAmount);
    }

    private static void deletePlayerClaimBlocks(EntityPlayer player) {
        try {
            List<String> fileData = Files.readAllLines(ModuleFTBUtils.PLAYER_RANKS.toPath());
            for (int i = 0; i < fileData.size(); i++) {
                if (fileData.get(i)
                        .startsWith("[" + player.getGameProfile().getId().toString())) {
                    fileData.remove(i);
                    fileData.remove(i);
                    fileData.remove(i);
                    i += 2;
                }
            }
            Files.write(ModuleFTBUtils.PLAYER_RANKS.toPath(), fileData,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
