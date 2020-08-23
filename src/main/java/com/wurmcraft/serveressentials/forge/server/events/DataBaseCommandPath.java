package com.wurmcraft.serveressentials.forge.server.events;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.json.rest.CommandQueue;
import com.wurmcraft.serveressentials.forge.api.json.rest.CommandQueue.RequestedCommand;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class DataBaseCommandPath {

  public static void startup() {
    ServerEssentialsServer.EXECUTORS.scheduleAtFixedRate(() -> {
          CommandQueue[] queue = RestRequestHandler.Commands.getCommandQueue();
          List<RequestedCommand> failedToRunCommands = new ArrayList<>();
          for (CommandQueue q : queue) {
            if (q.commands.length > 0 && q.commands[0].serverID
                .equalsIgnoreCase(SECore.config.serverID)) {
              for (RequestedCommand command : q.commands) {
                if (command.command.isEmpty()) {
                  continue;
                }
                if (command.requiredPlayer.isEmpty() || isPlayerOnline(
                    command.requiredPlayer)) {
                  if (SECore.config.debug) {
                    ServerEssentialsServer.LOGGER.info(
                        "Database has requested to run Command '" + command.command);
                  }
                  FMLCommonHandler.instance().getMinecraftServerInstance()
                      .getCommandManager()
                      .executeCommand(
                          FMLCommonHandler.instance().getMinecraftServerInstance(),
                          command.command);
                } else {
                  failedToRunCommands.add(command);
                }
              }
              if (failedToRunCommands.size() == 0) {
                failedToRunCommands.add(new RequestedCommand(SECore.config.serverID, "", ""));
              }
              RestRequestHandler.Commands.addCommandQueue(
                  new CommandQueue(failedToRunCommands.toArray(new RequestedCommand[0])));
              break;
            }
          }
        }, SECore.config.Rest.commandCheckTime, SECore.config.Rest.commandCheckTime,
        TimeUnit.SECONDS);
  }

  private static boolean isPlayerOnline(String uuid) {
    return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
        .getPlayers().stream()
        .anyMatch(player -> player.getGameProfile().getId().toString().equals(uuid));
  }

}
