package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.api.models.DelayedCommand;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.time.Instant;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import org.cliffc.high_scale_lib.NonBlockingHashSet;

public class DelayedCommandTicker {

  private static NonBlockingHashSet<DelayedCommand> toBeDeleted = new NonBlockingHashSet<>();

  @SubscribeEvent
  public void onServerTick(WorldTickEvent e) {
    if (e.world.getWorldTime() % 20 == 0) {
      for (DelayedCommand delayed : SECommand.delayedCommands) {
        if (delayed.pos != null) { // Player
          if (delayed.pos.equals(delayed.userData.player.getPosition())) {
            if (delayed.runAfter > Instant.now().getEpochSecond()) {
              delayed.seCommand.runCommand(
                  delayed.userData, delayed.sender, delayed.args, delayed.config);
              toBeDeleted.add(delayed);
            }
          } else {
            ChatHelper.send(delayed.sender, delayed.userData.lang.COMMAND_DELAY_MOVE);
          }
        } else { // Console
          delayed.seCommand.runCommand(
              delayed.userData, delayed.sender, delayed.args, delayed.config);
          toBeDeleted.add(delayed);
        }
      }
      // Deleted Completed Commands
      toBeDeleted.forEach(command -> SECommand.delayedCommands.remove(command));
    }
  }
}
