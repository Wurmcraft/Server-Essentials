package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.CommandConfig;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

public class SECommand extends CommandBase {

    public CommandConfig config;
    public Class<SECommand> instance;
    // Generated
    public Map<String, String> usageCache;    // lang-key, usage
    public Set<String[]> autoCompleteCache;
    public HashMap<CommandArgument[], Method> arguments;

    public SECommand(CommandConfig config, Class<SECommand> instance) throws NullPointerException {
        this.config = config;
        this.instance = instance;
        if (config == null || config.name == null || config.name.isEmpty())
            throw new NullPointerException("Invalid Command Name, Unable to make command");
        if (instance == null)   // TODO Check for valid command instance
            throw new NullPointerException("Invalid Command Class, Unable to make command");
        // TODO Implement Generation
        usageCache = new NonBlockingHashMap<>();
        autoCompleteCache = new HashSet<>();
        arguments = new HashMap<>();
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
        ServerPlayer userData = null;
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
        }
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
        Object[] converted = new Object[args.length];
        Method method = findMethod(args);
        if (method != null) {
            Command command = method.getDeclaredAnnotation(Command.class);
            for (int index = 0; index < command.args().length; index++)
                converted[index] = convert(args[index], command.args()[index]);
            try {
                Object output = method.invoke(this, player, converted);
                if (output instanceof Boolean)
                    return (boolean) output;
                if (output == null)
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
                LOG.info("Failed to execute command");
                // TODO Print Command Errored
                return false;
            }
        }
        return false;
    }

    private Method findMethod(String[] args) {
        return null;
    }

    private Object convert(String arg, CommandArgument type) {
        return type;
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
            if(config.secure) {
                // TODO Check if is secure user
            }
            Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT,((EntityPlayer) sender).getGameProfile().getId().toString(),new Account());
            return RankUtils.hasPermission(account, "command." + config.name);
        }
        return !config.secure;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
