package com.wurmcraft.serveressentials.common.utils;

import com.google.gson.Gson;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;

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
}
