package com.wurmcraft.serveressentials.common.modules.general.command.misc;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.modules.general.event.PlaytimeTrackerEvents;

@ModuleCommand(module = "General", name = "AFK", defaultAliases = {"AwayFromKeyboard"})
public class AFKCommand {

    @Command(args = {}, usage = {})
    public void afk(ServerPlayer player) {
        PlaytimeTrackerEvents.afk(player.player, !PlaytimeTrackerEvents.afkPlayers.contains(player.player));
    }
}
