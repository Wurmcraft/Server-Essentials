package com.wurmcraft.serveressentials.common.modules.core.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.event.PlayerLoadEvent;
import com.wurmcraft.serveressentials.api.event.PlayerUnloadEvent;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.account.BankAccount;
import com.wurmcraft.serveressentials.api.models.account.ServerTime;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

//@Mod.EventBusSubscriber(modid = ServerEssentials.MODID)
public class PlayerDataTrackerEvent {

    public static final String DEFAULT_RANK = "default";
    public static final String DEFAULT_LANG = "en_us";

    private static final NonBlockingHashMap<String, ScheduledFuture<?>> playerCacheTimeout = new NonBlockingHashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        try {
            Account userAccount = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, e.player.getGameProfile().getId().toString(), new Account());
            if (userAccount == null) {
                LOG.info("Loading user '" + e.player.getDisplayNameString() + "' (" + e.player.getGameProfile().getId().toString() + ") [New]");
                userAccount = new Account(e.player.getGameProfile().getId().toString(), e.player.getDisplayNameString(), new String[]{DEFAULT_RANK}, new String[0], new String[0], DEFAULT_LANG, false, 0, "", "", new ServerTime[]{new ServerTime(ServerEssentials.config.general.serverID, 0, Instant.now().getEpochSecond())}, new BankAccount[0], 0, "", "", new String[0]);
                if (SECore.dataLoader.register(DataLoader.DataType.ACCOUNT, e.player.getGameProfile().getId().toString(), userAccount)) {
                    LOG.info("Created New User ('" + e.player.getGameProfile().getId().toString() + "') \"" + e.player.getDisplayNameString() + "\"");
                    MinecraftForge.EVENT_BUS.post(new PlayerLoadEvent(e.player, userAccount, true));
                } else
                    LOG.warn("Failed to create new user");
            } else {
                LOG.info("Loading user '" + e.player.getDisplayNameString() + "' (" + e.player.getGameProfile().getId().toString() + ") [Existing]");
                MinecraftForge.EVENT_BUS.post(new PlayerLoadEvent(e.player, userAccount, false));
            }
            if (playerCacheTimeout.containsKey(e.player.getGameProfile().getId().toString())) {
                if (playerCacheTimeout.get(e.player.getGameProfile().getId().toString()).cancel(false))
                    LOG.debug("Canceling Cache cleanup for user '" + e.player.getDisplayNameString() + "' (" + e.player.getGameProfile().getId().toString() + ")");
            }
        } catch (Exception f) {
            f.printStackTrace();
            LOG.warn("Error while attempting to load user '" + e.player.getGameProfile().getId().toString() + "'");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        LOG.info("Scheduling unloading for user '" + e.player.getDisplayNameString() + "' (" + e.player.getGameProfile().getId().toString() + ") in " + ServerEssentials.config.performance.playerCacheTimeout + "s");
        Runnable scheduledTask = () -> {
            String uuid = e.player.getGameProfile().getId().toString();
            Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, uuid, new Account());
            if (!PlayerUtils.isUserOnline(uuid) && SECore.dataLoader.delete(DataLoader.DataType.ACCOUNT, uuid, true)) {
                MinecraftForge.EVENT_BUS.post(new PlayerUnloadEvent(account));
                LOG.info("Unloading user '" + UsernameCache.getLastKnownUsername(UUID.fromString(uuid)) + "' (" + uuid + ")");
            }
        };
        ScheduledFuture<?> future = ServerEssentials.scheduledService.schedule(scheduledTask, ServerEssentials.config.performance.playerCacheTimeout, TimeUnit.SECONDS);
        playerCacheTimeout.put(e.player.getGameProfile().getId().toString(), future);
    }
}
