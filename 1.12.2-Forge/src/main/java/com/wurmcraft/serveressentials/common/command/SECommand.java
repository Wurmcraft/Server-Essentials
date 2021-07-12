package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.CommandConfig;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Currency;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Home;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

public class SECommand extends CommandBase {

    public CommandConfig config;
    public Object instance;
    // Generated
    public Map<String, String> usageCache;    // lang-key, usage
    public HashMap<CommandArgument[], Method> arguments;

    public SECommand(CommandConfig config, Class<?> instance) throws NullPointerException, InstantiationException, IllegalAccessException {
        this.config = config;
        this.instance = instance.newInstance();
        if (config == null || config.name == null || config.name.isEmpty())
            throw new NullPointerException("Invalid Command Name, Unable to make command");
        if (instance == null)   // TODO Check for valid command instance
            throw new NullPointerException("Invalid Command Class, Unable to make command");
        for (Method method : instance.getDeclaredMethods())
            if (!isValidArguments(method.getDeclaredAnnotation(Command.class).args()))
                throw new NullPointerException("Invalid Command Arguments");
        // TODO Implement Generation
        usageCache = new NonBlockingHashMap<>();
        // Generate Command Arguments
        arguments = new HashMap<>();
        for (Method method : instance.getDeclaredMethods())
            if (method.isAnnotationPresent(Command.class))
                arguments.put(method.getDeclaredAnnotation(Command.class).args(), method);
    }

    public static boolean isValidArguments(CommandArgument[] arguments) {
        for (int index = 0; index < arguments.length; index++)
            if (arguments[index] == CommandArgument.STRING_ARR && index != arguments.length - 1)
                return false;
        return true;
    }

    @Override
    public String getName() {
        return config.name;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        String userLang = "en_us"; // TODO Get from sender
        String usage = usageCache.get(userLang);
        if (usage == null) {
            // TODO Find and generate usage
        }
        return usage;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!config.enabled) {
            // TODO Send Disabled
            return;
        }
        // TODO Check secure status
        ServerPlayer userData;
        if (sender instanceof EntityPlayer) {
            String uuid = ((EntityPlayer) sender).getGameProfile().getId().toString();
            userData = new ServerPlayer((EntityPlayer) sender, SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, uuid, new LocalAccount()), SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, uuid, new Account()));
            // Check Min Rank
            if (!config.minRank.isEmpty() && !RankUtils.isGreaterThan(config.minRank, userData.global.rank)) {
                // TODO Send perm error
            }
            // Currency Check
            if (!config.currencyCost.isEmpty()) {
                for (String name : config.currencyCost.keySet())
                    if (!EcoUtils.canBuy(name, config.currencyCost.get(name), userData.global)) {
                        // TODO Need currency msg
                        return;
                    }
            }
        } else
            userData = new ServerPlayer(sender);
        if (runMethod(userData, args)) {
            if (!config.currencyCost.isEmpty()) {
                // TODO Consume Currency
                // TODO Set cooldown
            }
        }
    }

    /**
     * @param player player data to send to the method
     * @param args   arguments for running the command
     * @return if the command has been executed successfully
     */
    private boolean runMethod(ServerPlayer player, String[] args) {
        Object[] converted = new Object[args.length + 1];
        converted[0] = player;
        Method method = findMethod(player, args);
        if (method != null) {
            Command command = method.getDeclaredAnnotation(Command.class);
            for (int index = 1; index < command.args().length + 1; index++) {
                // String arr must be the last argument
                if (command.args()[index - 1] == CommandArgument.STRING_ARR)
                    converted[index] = Arrays.copyOfRange(args, index, args.length);
                converted[index] = convert(player, args[index - 1], command.args()[index - 1]);
            }
            try {
                Object[] params = method.getParameterTypes();
                Object output = method.invoke(instance, converted);
                if (output instanceof Boolean)
                    return (boolean) output;
                if (output == null)
                    return true;
            } catch (Exception e) {
                LOG.info("Command: /" + getName() + " " + String.join(" ", args));
                e.printStackTrace();
                LOG.warn("Failed to execute command");
                // TODO Print Command Errored
                return false;
            }
        }
        return false;
    }

    private Method findMethod(ServerPlayer player, String[] args) {
        // Empty Case
        if (args.length == 0 && arguments.containsKey(new CommandArgument[0]))
            return arguments.get(new CommandArgument[0]);
        for (CommandArgument[] testArgs : arguments.keySet()) {
            // Size Match or has String Array
            if (testArgs.length == args.length || testArgs[testArgs.length - 1] == CommandArgument.STRING_ARR) {
                Object[] convertedArgs = convertArguments(player, testArgs, args);
                if (convertedArgs != null)
                    return arguments.get(testArgs);
            }
        }
        return null;
    }

    private Object[] convertArguments(ServerPlayer player, CommandArgument[] args, String[] userArgs) {
        try {
            if (args[args.length - 1] == CommandArgument.STRING_ARR) {
                List<Object> converted = new ArrayList<>();
                for (int index = 0; index < args.length; index++) {
                    if (args[index] != CommandArgument.STRING_ARR)
                        converted.add(convert(player, userArgs[index], args[index]));
                    else
                        converted.add(Arrays.copyOfRange(userArgs, index, userArgs.length));
                }
                return converted.toArray(new Object[0]);
            } else {
                Object[] converted = new Object[args.length];
                for (int index = 0; index < args.length; index++)
                    converted[index] = convert(player, userArgs[index], args[index]);
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
        }
        return null;
    }

    public EntityPlayer getPlayer(String name) {
        for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
            if (player.getDisplayNameString().equalsIgnoreCase(name))
                return player;
        // TODO Get based on nick
        return null;
    }

    // TODO Implement
    public Home getHome(ServerPlayer player, String name) {
        return null;
    }

    // TODO Implement
    public Location getWarp(String name) {
        return null;
    }

    public Currency getCurrency(String name) {
        // Attempt to get currency (match)
        for (String currency : SECore.dataLoader.getFromKey(DataLoader.DataType.CURRENCY, new Currency()).keySet())
            if (name.equalsIgnoreCase(currency))
                return SECore.dataLoader.get(DataLoader.DataType.CURRENCY, currency, new Currency());
        // Attempt to find based on starting match
        for (String currency : SECore.dataLoader.getFromKey(DataLoader.DataType.CURRENCY, new Currency()).keySet())
            if (currency.startsWith(name))
                return SECore.dataLoader.get(DataLoader.DataType.CURRENCY, currency, new Currency());
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
        }
        return aliases;
    }

    private static String upperFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(2).toLowerCase();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            if (config.secure) {
                // TODO Check if is secure user
            }
            Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, ((EntityPlayer) sender).getGameProfile().getId().toString(), new Account());
            return RankUtils.hasPermission(account, "command." + config.name);
        }
        return !config.secure;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
