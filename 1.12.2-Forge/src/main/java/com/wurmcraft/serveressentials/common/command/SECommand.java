package com.wurmcraft.serveressentials.common.command;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.CommandConfig;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.*;
import com.wurmcraft.serveressentials.api.models.Currency;
import com.wurmcraft.serveressentials.api.models.local.Home;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.modules.security.TrustedList;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.util.Strings;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.cliffc.high_scale_lib.NonBlockingHashSet;

public class SECommand extends CommandBase {

  public CommandConfig config;
  public Object instance;
  // Generated
  public String[] usage;
  public HashMap<CommandArgument[], Method> arguments;
  public HashMap<String, Method> subCommandArguments;

  public static NonBlockingHashSet<DelayedCommand> delayedCommands = new NonBlockingHashSet<DelayedCommand>();

  public SECommand(CommandConfig config, Class<?> instance)
      throws NullPointerException, InstantiationException, IllegalAccessException {
    this.config = config;
    this.instance = instance.newInstance();
    if (config == null || config.name == null || config.name.isEmpty()) {
      throw new NullPointerException("Invalid Command Name, Unable to make command");
    }
    if (!instance.isAnnotationPresent(ModuleCommand.class) && !instance.getAnnotation(
        ModuleCommand.class).name().isEmpty() && !instance.getAnnotation(
        ModuleCommand.class).module().isEmpty()) {
      throw new NullPointerException("Invalid Command Class, Unable to make command");
    }
    for (Method method : instance.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Command.class)
          && !isValidArguments(method.getDeclaredAnnotation(Command.class).args())) {
        throw new NullPointerException("Invalid Command Arguments");
      }
    }
    // Generate Command Arguments
    arguments = new HashMap<>();
    subCommandArguments = new HashMap<>();
    for (Method method : instance.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Command.class)) {
        Command command = method.getDeclaredAnnotation(Command.class);
        if (command.isSubCommand()) {
          subCommandArguments.put(method.getName(), method);
          for (String subAliases : command.subCommandAliases()) {
            subCommandArguments.put(subAliases, method);
          }
        } else {
          arguments.put(command.args(), method);
        }
      }
    }
    // Create Usage
    List<String> usageArr = new ArrayList<>();
    for (CommandArgument[] arg : arguments.keySet()) {
      String temp = "/" + getName();
      for (int index = 0; index < arg.length; index++) {
        CommandArgument a = arg[index];
        Command command = arguments.get(arg).getDeclaredAnnotation(Command.class);
        if (a.equals(CommandArgument.STRING) && command.usage().length > index) {
          temp = temp + " <" + command.usage()[index] + ">";
        } else {
          temp = temp + " <" + a.name().toLowerCase() + ">";
        }
      }
      usageArr.add(temp);
    }
    // Sub Command Usage
    for (String sub : subCommandArguments.keySet()) {
      String temp = "/" + getName() + " " + sub;
      Method method = subCommandArguments.get(sub);
      Command command = method.getDeclaredAnnotation(Command.class);
      for (int index = 0; index < command.args().length; index++) {
        if (command.args()[index].equals(CommandArgument.STRING)
            && command.usage().length > index) {
          temp = temp + " <" + command.usage()[index] + ">";
        } else {
          temp = temp + " <" + command.args()[index].name().toLowerCase() + ">";
        }
      }
      usageArr.add(temp);
    }
    usage = usageArr.toArray(new String[0]);
  }

  public static boolean isValidArguments(CommandArgument[] arguments) {
    for (int index = 0; index < arguments.length; index++) {
      if (arguments[index] == CommandArgument.STRING_ARR
          && index != arguments.length - 1) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String getName() {
    return config.name;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return Strings.join(Arrays.asList(usage), '\n');
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
    ServerPlayer userData;
    if (sender instanceof EntityPlayer) {
      String uuid = ((EntityPlayer) sender).getGameProfile().getId().toString();
      userData =
          new ServerPlayer(
              (EntityPlayer) sender,
              SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, uuid,
                  new LocalAccount()),
              SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, uuid, new Account()));
      if (!config.enabled) {
        ChatHelper.send(sender, userData.lang.DISABLED);
        return;
      }
      // Check Min Rank
      if (!config.minRank.isEmpty()
          && !RankUtils.isGreaterThan(config.minRank, userData.global.rank)) {
        ChatHelper.send(sender,
            new TextComponentTranslation("commands.generic.permission"));
        return;
      }
      // Currency Check
      if (!config.currencyCost.isEmpty()) {
        for (String name : config.currencyCost.keySet()) {
          if (!EcoUtils.canBuy(name, config.currencyCost.get(name), userData.global)) {
            ChatHelper.send(sender, userData.lang.NO_MONEY);
            return;
          }
        }
      }
      // Check for trusted user
      if (config.secure && !TrustedList.trustedUsers.contains(uuid)) {
        ChatHelper.send(sender,
            new TextComponentTranslation("commands.generic.permission"));
        LOG.debug(
            "User '" + ((EntityPlayer) sender).getDisplayNameString() + "' tried to run '"
                + config.name + "' however its a secure command, preventing");
      }
    } else {
      userData = new ServerPlayer(sender);
      if (!config.enabled) {
        ChatHelper.send(sender, userData.lang.DISABLED);
        return;
      }
      if (config.secure) {
        ChatHelper.send(sender,
            new TextComponentTranslation("commands.generic.permission"));
        return;
      }
      runCommand(userData, sender, args, config);
    }
    // Check for command cooldown
    long rankDelay = 0;
    if (userData.sender instanceof EntityPlayer) {
      rankDelay = CommandUtils.getLowest(userData.global.rank, config.rankDelay);
    }
    if (rankDelay == 0) {
      if (!(userData.sender instanceof EntityPlayer)
          || !userData.local.commandUsage.containsKey(config.name)
          || Instant.now().getEpochSecond() > userData.local.commandUsage.get(
          config.name)) {
        runCommand(userData, sender, args, config);
      } else {
        ChatHelper.send(sender, userData.lang.COMMAND_COOLDOWN.replaceAll("\\{@TIME@}",
            "" + (userData.local.commandUsage.get(config.name) - Instant.now()
                .getEpochSecond())));
      }
    } else {
      DelayedCommand delayedCommand = new DelayedCommand(userData, sender, args, config,
          this, sender.getCommandSenderEntity() instanceof EntityPlayer
          ? userData.player.getPosition() : null,
          Instant.now().getEpochSecond() + rankDelay);
      delayedCommands.add(delayedCommand);
      ChatHelper.send(sender,
          userData.lang.COMMAND_DELAY.replaceAll("\\{@TIME@}", "" + rankDelay));
      runCommand(userData, sender, args, config);
    }
  }

  public void runCommand(ServerPlayer userData, ICommandSender sender, String[] args,
      CommandConfig config) {
    // Run Command
    if (runMethod(userData, args)) {
      if (!config.currencyCost.isEmpty()) {
        for (String name : config.currencyCost.keySet()) {
          EcoUtils.buy(userData.global, name, config.currencyCost.get(name));
        }
      }
      if (userData.sender instanceof EntityPlayer) {
        userData.local.commandUsage.put(config.name,
            Instant.now().getEpochSecond() + CommandUtils.getLowest(userData.global.rank,
                config.rankCooldown));
        SECore.dataLoader.update(DataType.LOCAL_ACCOUNT, userData.local.uuid,
            userData.local);
      }
    } else {
      ChatHelper.send(sender, getUsage(sender));
    }
  }

  /**
   * @param player player data to send to the method
   * @param args arguments for running the command
   * @return if the command has been executed successfully
   */
  private boolean runMethod(ServerPlayer player, String[] args) {
    Method method = findMethod(player, args);
    if (method != null) {
      Command command = method.getDeclaredAnnotation(Command.class);
      if (!command.canConsoleUse() && player.player == null) {
        ChatHelper.send(player.sender, player.lang.PLAYER_ONLY);
        return true;
      }
      List<Object> converted = new ArrayList<>();
      converted.add(player);
      Object[] conv =
          convertArguments(
              player,
              command.args(),
              command.isSubCommand() ? Arrays.copyOfRange(args, 1, args.length) : args);
      if (conv != null) {
        converted.addAll(Arrays.asList(conv));
      }
      try {
        Object output = method.invoke(instance, converted.toArray(new Object[0]));
        if (output instanceof Boolean) {
          return (boolean) output;
        }
        if (output == null) {
          return true;
        }
      } catch (Exception e) {
        LOG.info("Command: /" + getName() + " " + String.join(" ", args));
        e.printStackTrace();
        LOG.warn("Failed to execute command");
        ChatHelper.send(player.sender, player.lang.COMMAND_ERROR);
        return false;
      }
    }
    return false;
  }

  private Method findMethod(ServerPlayer player, String[] args) {
    // Empty Case
    if (args.length == 0) {
      for (CommandArgument[] a : arguments.keySet()) {
        if (a == null || a.length == 0) {
          return arguments.get(a);
        }
      }
      return null;
    }
    // Sub Command Search
    if (subCommandArguments.size() > 0 && args.length > 0) {
      for (String arg : subCommandArguments.keySet()) {
        if (arg.equalsIgnoreCase(args[0])
            && (args.length - 1)
            == subCommandArguments
            .get(arg)
            .getDeclaredAnnotation(Command.class)
            .args()
            .length) {
          Object[] convertedArgs =
              convertArguments(
                  player,
                  subCommandArguments.get(arg).getDeclaredAnnotation(Command.class)
                      .args(),
                  Arrays.copyOfRange(args, 1, args.length));
          if (convertedArgs != null
              || subCommandArguments.get(arg).getDeclaredAnnotation(Command.class)
              .args().length
              == 0) {
            return subCommandArguments.get(arg);
          }
        }
      }
    }
    // Default Search
    for (CommandArgument[] testArgs : arguments.keySet()) {
      // Size Match or has String Array
      if (testArgs.length == args.length
          || testArgs.length > 0
          && testArgs[testArgs.length - 1] == CommandArgument.STRING_ARR) {
        Object[] convertedArgs = convertArguments(player, testArgs, args);
        if (convertedArgs != null) {
          return arguments.get(testArgs);
        }
      }
    }
    return null;
  }

  private Object[] convertArguments(
      ServerPlayer player, CommandArgument[] args, String[] userArgs) {
    try {
      if (args[args.length - 1] == CommandArgument.STRING_ARR) {
        List<Object> converted = new ArrayList<>();
        for (int index = 0; index < args.length; index++) {
          if (args[index] != CommandArgument.STRING_ARR) {
            converted.add(convert(player, userArgs[index], args[index]));
          } else {
            converted.add(Arrays.copyOfRange(userArgs, index, userArgs.length));
          }
        }
        return converted.toArray(new Object[0]);
      } else {
        Object[] converted = new Object[args.length];
        for (int index = 0; index < args.length; index++) {
          converted[index] = convert(player, userArgs[index], args[index]);
        }
        return converted;
      }
    } catch (Exception e) {
    }
    return null;
  }

  private Object convert(ServerPlayer player, String arg, CommandArgument type) {
    if (type == CommandArgument.INTEGER) {
      return Integer.parseInt(arg);
    } else if (type == CommandArgument.DOUBLE) {
      return Double.parseDouble(arg);
    } else if (type == CommandArgument.PLAYER) {
      return getPlayer(arg);
    } else if (type == CommandArgument.STRING || type == CommandArgument.MODULE) {
      return arg;
    } else if (type == CommandArgument.RANK) {
      return SECore.dataLoader.get(DataLoader.DataType.RANK, arg, new Rank());
    } else if (type == CommandArgument.HOME) {
      return getHome(player, arg);
    } else if (type == CommandArgument.WARP) {
      return getWarp(arg);
    } else if (type == CommandArgument.CURRENCY) {
      return getCurrency(arg);
    } else if (type == CommandArgument.DATA_TYPE) {
      return DataLoader.DataType.valueOf(arg.toUpperCase());
    } else if (type == CommandArgument.CHANNEL) {
      return getChannel(arg);
    } else if (type == CommandArgument.KIT) {
      return getKit(arg);
    }
    return null;
  }

  private Kit getKit(String arg) {
    Kit[] kits =
        SECore.dataLoader
            .getFromKey(DataLoader.DataType.KIT, new Kit())
            .values()
            .toArray(new Kit[0]);
    for (Kit kit : kits) {
      if (kit.name.equalsIgnoreCase(arg)) {
        return kit;
      }
    }
    return null;
  }

  public EntityPlayer getPlayer(String name) {
    for (EntityPlayer player :
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
            .getPlayers()) {
      Account account = SECore.dataLoader.get(DataType.ACCOUNT,
          player.getGameProfile().getId().toString(), new Account());
      if (player.getDisplayNameString().equalsIgnoreCase(name) || name.equalsIgnoreCase(
          ChatHelper.getName(player, account))) {
        return player;
      }
    }
    return null;
  }

  public Home getHome(ServerPlayer player, String name) {
    Home[] homes = player.local.homes;
    for (Home home : homes) {
      if (home.name.equalsIgnoreCase(name)) {
        return home;
      }
    }
    return null;
  }

  public Warp getWarp(String name) {
    Warp[] warps =
        SECore.dataLoader
            .getFromKey(DataLoader.DataType.WARP, new Warp[0])
            .values()
            .toArray(new Warp[0]);
    for (Warp warp : warps) {
      if (warp.name.startsWith(name)) {
        return warp;
      }
    }
    return null;
  }

  public Currency getCurrency(String name) {
    // Attempt to get currency (match)
    for (String currency :
        SECore.dataLoader.getFromKey(DataLoader.DataType.CURRENCY, new Currency())
            .keySet()) {
      if (name.equalsIgnoreCase(currency)) {
        return SECore.dataLoader.get(DataLoader.DataType.CURRENCY, currency,
            new Currency());
      }
    }
    // Attempt to find based on starting match
    for (String currency :
        SECore.dataLoader.getFromKey(DataLoader.DataType.CURRENCY, new Currency())
            .keySet()) {
      if (currency.startsWith(name)) {
        return SECore.dataLoader.get(DataLoader.DataType.CURRENCY, currency,
            new Currency());
      }
    }
    return null;
  }

  public Channel getChannel(String name) {
    // Direct Match
    Channel ch = SECore.dataLoader.get(DataLoader.DataType.CHANNEL, name, new Channel());
    if (ch != null) {
      return ch;
    }
    // Attempt to get currency (match)
    for (String channel :
        SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel())
            .keySet()) {
      if (name.equalsIgnoreCase(channel)) {
        return SECore.dataLoader.get(DataLoader.DataType.CURRENCY, channel,
            new Channel());
      }
    }
    // Attempt to find based on starting match
    for (String channel :
        SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel())
            .keySet()) {
      if (channel.startsWith(name)) {
        return SECore.dataLoader.get(DataLoader.DataType.CHANNEL, channel, new Channel());
      }
    }
    return null;
  }

  @Override
  public List<String> getAliases() {
    List<String> aliases = new ArrayList<>();
    aliases.add(config.name.toLowerCase());
    aliases.add(config.name.toUpperCase());
    aliases.add(upperFirstLetter(config.name));
    for (String ali : config.aliases) {
      aliases.add(ali.toLowerCase());
      aliases.add(ali.toUpperCase());
      aliases.add(upperFirstLetter(ali));
      if (!aliases.contains(ali)) {
        aliases.add(ali);
      }
    }
    return aliases;
  }

  private static String upperFirstLetter(String str) {
    if (str.length() > 1) {
      return str.substring(0, 1).toUpperCase() + str.substring(2).toLowerCase();
    }
    return str;
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    if (sender instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender;
      Account account = SECore.dataLoader.get(DataType.ACCOUNT,
          player.getGameProfile().getId().toString(), new Account());
      return RankUtils.hasPermission(account, config.permissionNode);
    }
    return !config.secure;
  }

  @Override
  public List<String> getTabCompletions(
      MinecraftServer server, ICommandSender sender, String[] args,
      @Nullable BlockPos targetPos) {
    int currentIndex = 0;
    CommandArgument arg;
    if (!args[0].isEmpty()) {
      for (int index = 0; index < args.length; index++) {
        if (args[index].isEmpty()) {
          currentIndex = index;
        }
      }
    }
    List<String> tabComplete = new ArrayList<>();
    // Autofill arguments
    for (CommandArgument[] a : arguments.keySet()) {
      Command command = arguments.get(a).getDeclaredAnnotation(Command.class);
      if (a.length > currentIndex) {
        arg = command.args()[currentIndex];
        tabComplete.addAll(
            CommandUtils.predict(
                args[currentIndex],
                CommandUtils.generatePossibleAutoFill(sender, arg,
                    command.usage()[currentIndex])));
      }
    }
    if (args[0].isEmpty()) {
      for (String sub : subCommandArguments.keySet()) {
        tabComplete.addAll(CommandUtils.predict(args[0], Collections.singletonList(sub)));
      }
    } else if (subCommandArguments.containsKey(args[0].toLowerCase())) {
      Command command =
          subCommandArguments.get(args[0].toLowerCase())
              .getDeclaredAnnotation(Command.class);
      if (command.args().length > (currentIndex - 1)) {
        arg = command.args()[currentIndex - 1];
        tabComplete.addAll(
            CommandUtils.predict(
                args[currentIndex],
                CommandUtils.generatePossibleAutoFill(
                    sender, arg, command.usage()[currentIndex - 1])));
      }
    }
    return tabComplete;
  }
}
