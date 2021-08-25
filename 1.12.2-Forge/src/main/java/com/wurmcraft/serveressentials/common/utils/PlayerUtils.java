package com.wurmcraft.serveressentials.common.utils;

import com.google.gson.Gson;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.local.Home;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.UUID;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;

import java.util.*;

public class PlayerUtils {

    public static boolean isUserOnline(String uuid) {
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
            if (player.getGameProfile().getId().toString().equals(uuid))
                return true;
        return false;
    }

    private static String validateUUID(String input) {
        // UUID Check
        try {
            UUID uuid = UUID.fromString(input);
            String username = UsernameCache.getLastKnownUsername(uuid);
            if (username != null)
                return uuid.toString();
        } catch (Exception e) {
        }
        // Username Check
        if (UsernameCache.getMap().containsValue(input))
            for (UUID uuid : UsernameCache.getMap().keySet())
                if (UsernameCache.getMap().get(uuid).equals(input))
                    return uuid.toString();
        return null;
    }

    private static String validateUUIDRemote(String input) {
        // UUID Check
        try {
            UUID uuid = UUID.fromString(input);
            RequestGenerator.HttpResponse response = RequestGenerator.get("api/lookup/username/" + uuid);
            if (response.status == 200) {
                Account account = GSON.fromJson(response.response, Account.class);
                String username = account.username;
                if (username != null)
                    return uuid.toString();
            }
        } catch (Exception e) {
        }
        // Username Check
        try {
            RequestGenerator.HttpResponse response = RequestGenerator.get("api/lookup/uuid/" + input);
            if (response.status == 200) {
                Account account = GSON.fromJson(response.response, Account.class);
                String uuid = account.uuid;
                if (uuid != null)
                    return uuid;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String getUUIDForInput(String input) {
        String localUUID = validateUUID(input);
        if (localUUID == null && SECore.dataLoader.getClass().equals(RestDataLoader.class))
            return validateUUIDRemote(input);
        return localUUID;
    }

    private static String validateUsername(String uuid) {
        try {
            return UsernameCache.getLastKnownUsername(UUID.fromString(uuid));
        } catch (Exception e) {
        }
        return null;
    }

    private static String validateUsernameRemote(String uuid) {
        try {
            RequestGenerator.HttpResponse response = RequestGenerator.get("api/lookup/username/" + uuid);
            if (response.status == 200) {
                Account account = GSON.fromJson(response.response, Account.class);
                return account.username;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String getUsernameForInput(String input) {
        // Validate UUID
        try {
            UUID uuid = UUID.fromString(input);
            String username = validateUsername(uuid.toString());
            if (username == null && SECore.dataLoader.getClass().equals(RestDataLoader.class))
                return validateUsernameRemote(uuid.toString());
        } catch (Exception e) {
        }
        // Validate Username
        String username = validateUsername(input);
        if (username != null)
            return username;
        else if (SECore.dataLoader.getClass().equals(RestDataLoader.class))
            return validateUsernameRemote(validateUUIDRemote(input));
        return null;
    }

    public static EntityPlayer getFromUUID(String uuid) {
        try {
            return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(uuid));
        } catch (Exception e) {

        }
        return null;
    }

    public static Home getHome(LocalAccount local, String name) {
        if (local.homes != null)
            for (Home home : local.homes)
                if (name.equalsIgnoreCase(home.name))
                    return home;
        return null;
    }

    // TODO Implement
    public static int maxHomes(Account global) {
        return 1;
    }

    public static Location getSpawn(String[] ranks) {
        HashMap<String, Location> spawnPos = ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).spawn;
        // Simple Match (if possible)
        if (ranks.length == 1)
            return spawnPos.getOrDefault(ranks[0], null);
        // Highest Rank
        List<Rank> userRanks = new ArrayList<>();
        for (String name : ranks) {
            Rank rank = SECore.dataLoader.get(DataLoader.DataType.RANK, name, new Rank());
            if (rank != null && spawnPos.containsKey(rank.name))
                userRanks.add(rank);
        }
        if (userRanks.size() > 0) {
            Rank highestRank = userRanks.get(0);
            for (Rank rank : userRanks)
                if (RankUtils.isGreaterThan(rank, highestRank))
                    highestRank = rank;
            return spawnPos.get(highestRank.name);
        }
        if (spawnPos.containsKey("*"))
            return spawnPos.get("*");
        return null;
    }
}
