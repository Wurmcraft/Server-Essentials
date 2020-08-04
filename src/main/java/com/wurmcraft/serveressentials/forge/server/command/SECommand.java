package com.wurmcraft.serveressentials.forge.server.command;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.Home;
import com.wurmcraft.serveressentials.forge.modules.core.event.CoreEvents;
import com.wurmcraft.serveressentials.forge.modules.core.utils.CoreUtils;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParams;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParams.RankParams;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.CommandParser;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.util.Strings;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class SECommand extends CommandBase {

  private ModuleCommand command;
  private Object commandInstance;

  // Cache
  private NonBlockingHashMap<CommandArguments[], Method> cache;
  private NonBlockingHashMap<String, CommandArguments> argumentCache;
  private CommandParams params;

  public SECommand(ModuleCommand command, Object commandInstance) {
    this.command = command;
    this.commandInstance = commandInstance;
    cache = new NonBlockingHashMap<>();
    argumentCache = new NonBlockingHashMap<>();
    for (Method method : commandInstance.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(Command.class)) {
        Command commandAnnotation = method.getAnnotation(Command.class);
        cache.put(commandAnnotation.inputArguments(), method);
      }
    }
    params = CoreUtils.getParams(command.moduleName() + "." + command.name());
  }

  @Override
  public String getName() {
    return command.name();
  }

  @Override
  public List<String> getAliases() {
    List<String> aliases = new ArrayList<>();
    aliases.add(command.name().toLowerCase());
    aliases.add(command.name().toUpperCase());
    aliases.add(command.name());
    if (command.aliases().length > 0) {
      for (String a : command.aliases()) {
        aliases.add(a);
        aliases.add(a.toLowerCase());
        aliases.add(a.toUpperCase());
      }
    }
    return aliases;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/" + command.name();
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    boolean hasPerms = RankUtils
        .hasPermission(sender, command.moduleName() + "." + command.name().toLowerCase());
    return hasPerms;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      String[] args, @Nullable BlockPos targetPos) {
    return super.getTabCompletions(server, sender, args, targetPos);
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    if (SECore.config.logCommandsToConsole) {
      ServerEssentialsServer.LOGGER.info(
          sender.getDisplayName().getUnformattedText() + " has run command '/" + command
              .name() + " " + Strings
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
          if (rankParams.cost != null && rankParams.cost.amount > 0) {
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
                .getCommandCooldown(player, command.moduleName() + "." + command.name());
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
                      .setCooldown(player, command.moduleName() + "." + command.name(),
                          finalRankParams.cooldownTime);
                }
                CoreEvents.moveTracker.remove(player.getGameProfile().getId());
                runBasicCommand(sender, args);
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
                    .setCooldown(player, command.moduleName() + "." + command.name(),
                        rankParams.cooldownTime);
              }
              runBasicCommand(sender, args);
            }
          }
        } else {
          runBasicCommand(sender, args);
        }
      } else {
        ChatHelper.sendMessage(sender, TextFormatting.RED +
            "Console is not supported by this command!");
      }
    } else {
      runBasicCommand(sender, args);
    }
  }

  private void runBasicCommand(ICommandSender sender, String[] args) {
    Object[] handler = findMatch(sender, cache.keySet(), args);
    if ((boolean) handler[0]) {
      try {
        Object[] commandArgs = CommandParser.parseLineToArguments(sender, args,
            ((Method) handler[1]).getDeclaredAnnotation(Command.class)
                .inputArguments());
        ((Method) handler[1]).invoke(commandInstance, commandArgs);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    } else {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_USAGE
          .replaceAll("%COMMAND%", command.name()));
    }
  }

  private Object[] findMatch(ICommandSender sender, Set<CommandArguments[]> methodArgs,
      String[] args) {
    CommandArguments[] exactMatch = findExactMatch(sender, methodArgs, args);
    if (exactMatch != null) {
      return new Object[]{true, cache.get(exactMatch)};
    } else {
      CommandArguments[] strArr = findStringArray(methodArgs, args);
      if (strArr != null) {
        return new Object[]{true, cache.get(strArr)};
      } else {
        CommandArguments[] fussyMatch = findFussyMatch(sender, methodArgs, args);
        if (fussyMatch != null) {
          return new Object[]{true, cache.get(fussyMatch)};
        }
      }
    }
    return new Object[]{false, null};
  }

  private CommandArguments[] findExactMatch(ICommandSender sender,
      Set<CommandArguments[]> methodArgs,
      String[] args) {
    for (CommandArguments[] ca : methodArgs) {
      if (ca.length == args.length) {
        boolean valid = true;
        for (int index = 0; index < ca.length; index++) {
          CommandArguments inputType = getArgumentType(sender, args[index]);
          if (SECore.config.debug) {
            ServerEssentialsServer.LOGGER.debug(
                Arrays.toString(ca) + " => " + ca[index] + " " + inputType + " ("
                    + args[index] + ")");
          }
          if (!ca[index].equals(inputType)) {
            valid = false;
          }
        }
        if (valid) {
          return ca;
        }
      }
    }
    return null;
  }

  private CommandArguments[] findStringArray(Set<CommandArguments[]> methodArgs,
      String[] args) {
    for (CommandArguments[] ca : methodArgs) {
      if (ca.length == 1 && ca[0].equals(CommandArguments.STRING_ARR)) {
        return ca;
      }
    }
    return null;
  }

  private CommandArguments[] findFussyMatch(ICommandSender sender,
      Set<CommandArguments[]> methodArgs,
      String[] args) {
    for (CommandArguments[] ca : methodArgs) {
      if (ca.length == args.length) {
        boolean valid = true;
        for (int index = 0; index < ca.length; index++) {
          CommandArguments inputType = getArgumentType(sender, args[index]);
          if (ca[index].equals(CommandArguments.STRING) && inputType.stringable) {

          } else {
            valid = false;
          }
        }
        if (valid) {
          return ca;
        }
      }
    }
    return null;
  }

  private CommandArguments getArgumentType(ICommandSender sender, String line) {
    if (argumentCache.containsKey(line.toUpperCase())) {
      return argumentCache.get(line.toUpperCase());
    }
    if (isInteger(line)) {
      return CommandArguments.INTEGER;
    } else if (isDouble(line)) {
      return CommandArguments.DOUBLE;
    } else if (isPlayerUsername(line)) {
      return CommandArguments.PLAYER;
    } else if (isRank(line)) {
      return CommandArguments.RANK;
    } else if (isModule(line)) {
      return CommandArguments.MODULE;
    } else if (sender.getCommandSenderEntity() instanceof EntityPlayer && isHome(
        (EntityPlayer) sender.getCommandSenderEntity(), line)) {
      return CommandArguments.HOME;
    } else {
      return CommandArguments.STRING;
    }
  }

  private boolean isInteger(String line) {
    try {
      Integer.parseInt(line);
      argumentCache.put(line.toUpperCase(), CommandArguments.INTEGER);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private boolean isDouble(String line) {
    try {
      Double.parseDouble(line);
      argumentCache.put(line.toUpperCase(), CommandArguments.DOUBLE);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private boolean isPlayerUsername(String line) {
    for (String name : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getOnlinePlayerNames()) {
      if (line.equalsIgnoreCase(name)) {
        argumentCache.put(line.toUpperCase(), CommandArguments.PLAYER);
        return true;
      }
    }
    return false;
  }

  private boolean isRank(String line) {
    try {
      for (Rank rank : SECore.dataHandler.getDataFromKey(DataKey.RANK, new Rank())
          .values()) {
        if (rank.getID().equalsIgnoreCase(line)) {
          return true;
        }
      }
    } catch (Exception ignored) {
    }
    return false;
  }

  private boolean isHome(EntityPlayer player, String line) {
    Home[] homes = PlayerUtils.get(player).server.homes;
    for (Home home : homes) {
      if (home.name.equalsIgnoreCase(line)) {
        return true;
      }
    }
    return false;
  }

  private boolean isModule(String line) {
    return ModuleLoader.getLoadedModule(line) != null;
  }

}
