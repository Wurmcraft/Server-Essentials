package com.wurmcraft.serveressentials.common.modules.core.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.account.BankAccount;
import com.wurmcraft.serveressentials.api.models.account.ServerTime;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.time.Instant;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

@Mod.EventBusSubscriber(modid = ServerEssentials.MODID)
public class PlayerDataTrackerEvent {

    public static final String DEFAULT_RANK = "default";
    public static final String DEFAULT_LANG = "en_us";

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        try {
            LOG.debug("Loading user '" + e.player.getGameProfile().getId().toString() + "'");
            Account userAccount = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, e.player.getGameProfile().getId().toString(), new Account());
            if (userAccount == null) {
                LOG.info("New Account has logged in");
                userAccount = new Account(e.player.getGameProfile().getId().toString(), e.player.getDisplayNameString(), new String[]{DEFAULT_RANK}, new String[0], new String[0], DEFAULT_LANG, false, 0, "", "", new ServerTime[]{new ServerTime(ServerEssentials.config.general.serverID, 0, Instant.now().getEpochSecond())}, new BankAccount[0], 0, "", "", new String[0]);
                if (SECore.dataLoader.register(DataLoader.DataType.ACCOUNT, e.player.getGameProfile().getId().toString(), userAccount)) {
                    LOG.info("Created New User");
                } else {
                    LOG.warn("Failed to create new user");
                }
            } else {
                LOG.info("Existing User Logged in");
            }
        } catch (Exception f) {
            f.printStackTrace();
            LOG.warn("Error while attempting to load user '" + e.player.getGameProfile().getId().toString() + "'");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {

    }
}
