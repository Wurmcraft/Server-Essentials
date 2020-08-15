package com.wurmcraft.serveressentials.forge.server.command;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.modules.core.event.CoreEvents;
import com.wurmcraft.serveressentials.forge.modules.core.utils.CoreUtils;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParams;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParams.RankParams;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.util.Strings;

public class WrapperCommand extends CommandBase {

  private ICommand command;
  private CommandParams params;

  public WrapperCommand(ICommand command) {
    this.command = command;
    params = CoreUtils.getParams("command." + command.getName());
  }

  @Override
  public String getName() {
    return command.getName();
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return command.getUsage(sender);
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    if (RankUtils.hasPermission(sender, "command." + command.getName())) {
      if (SECore.config.logCommandsToConsole) {
        ServerEssentialsServer.LOGGER.info(
            sender.getDisplayName().getUnformattedText() + " has run command '/" + command
                .getName() + Strings
                .join(Arrays.asList(args), ' ') + "'");
      }
      if (params != null && !params.ranks.isEmpty()
          && ModuleLoader.getLoadedModule("Rank") != null) {
        Rank rank = RankUtils.getRank(sender);
        if (rank != null && sender.getCommandSenderEntity() instanceof EntityPlayer) {
          EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
          if (params.ranks.containsKey(rank.getID().toLowerCase()) || params.ranks
              .containsKey("*")) {
            RankParams rankParams = null;
            if (params.ranks.containsKey(rank.getID().toLowerCase())) {
              rankParams = params.ranks.get(rank.getID().toLowerCase());
            } else {
              rankParams = params.ranks.get("*");
            }
            boolean canRunCommand = true;
            boolean updateMoney = false;
            boolean updateCooldown = false;
            if (rankParams.cost.amount > 0) {
              if (EcoUtils
                  .hasCurrency(player, rankParams.cost.name, rankParams.cost.amount)) {
                updateMoney = true;
              } else {
                canRunCommand = false;
                ChatHelper.sendMessage(sender,
                    PlayerUtils.getLanguage(sender).ECO_MONEY_INSUFFICENT
                        .replaceAll("%AMOUNT%", "" + rankParams.cost.amount));
                return;
              }
            }
            if (rankParams.cooldownTime > 0) {
              long nextRun = PlayerUtils
                  .getCommandCooldown(player, "command." + command.getName());
              if (nextRun <= System.currentTimeMillis()) {
                updateCooldown = true;
              } else {
                canRunCommand = false;
                ChatHelper.sendMessage(sender,
                    PlayerUtils.getLanguage(player).COMMAND_COOLDOWN
                        .replaceAll("%AMOUNT%",
                            "" + ((nextRun - System.currentTimeMillis()) / 1000)));
                return;
              }
            }
            if (rankParams.windupTime > 0) {
              CoreEvents.moveTracker.put(player.getGameProfile().getId(),
                  new LocationWrapper(player.posX, player.posY, player.posZ,
                      player.dimension));
              ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_MOVE
                  .replaceAll("%AMOUNT%", "" + rankParams.windupTime));
              // Thread Specific Vars
              boolean finalCanRunCommand = canRunCommand;
              boolean finalUpdateMoney = updateMoney;
              RankParams finalRankParams = rankParams;
              boolean finalUpdateCooldown = updateCooldown;
              ServerEssentialsServer.EXECUTORS.schedule(() -> {
                if (finalCanRunCommand) {
                  if (finalUpdateMoney) {
                    EcoUtils.consumeCurrency(player, finalRankParams.cost.name,
                        finalRankParams.cost.amount);
                    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).ECO_DEL
                        .replaceAll("%AMOUNT%", "" + finalRankParams.cost.amount)
                        .replaceAll("%PLAYER%", player.getDisplayNameString()));
                  }
                  if (finalUpdateCooldown) {
                    PlayerUtils
                        .setCooldown(player, "command." + command.getName(),
                            finalRankParams.cooldownTime);
                  }
                  CoreEvents.moveTracker.remove(player.getGameProfile().getId());
                  try {
                    command.execute(server, sender, args);
                  } catch (CommandException ignored) {
                  }
                }
              }, rankParams.windupTime, TimeUnit.SECONDS);
            } else {
              if (canRunCommand) {
                if (updateMoney) {
                  EcoUtils.consumeCurrency(player, rankParams.cost.name,
                      rankParams.cost.amount);
                  ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).ECO_DEL
                      .replaceAll("%AMOUNT%", "" + rankParams.cost.amount)
                      .replaceAll("%PLAYER%", player.getDisplayNameString()));
                }
                if (updateCooldown) {
                  PlayerUtils
                      .setCooldown(player, "command." + command.getName(),
                          rankParams.cooldownTime);
                }
                command.execute(server, sender, args);
              }
            }
          } else {
            command.execute(server, sender, args);
          }
        } else {
          ChatHelper.sendMessage(sender, TextFormatting.RED +
              "Console is not supported by this command!");
        }
      } else {
        command.execute(server, sender, args);
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "command." + command.getName());
    }
  }

  @Override
  public List<String> getAliases() {
    return command.getAliases();
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    boolean canRun =  RankUtils.hasPermission(sender, "command." + getName());
    if(!canRun) {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "command." + command.getName());
    }
    return canRun;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      String[] args, BlockPos targetPos) {
    return command.getTabCompletions(server, sender, args, targetPos);
  }

  @Override
  public boolean isUsernameIndex(String[] args, int index) {
    return command.isUsernameIndex(args, index);
  }
}