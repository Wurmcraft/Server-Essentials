package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.CommandConfig;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Currency;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;
import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

public class CommandUtils {

    public static File COMMAND_SAVE_DIR = new File(SAVE_DIR + File.separator + "Commands");

    public static CommandConfig loadConfig(ModuleCommand moduleCommand) {
        String name = moduleCommand.name().toLowerCase();
        File command = new File(COMMAND_SAVE_DIR + File.separator + name.toLowerCase() + ".json");
        if (!COMMAND_SAVE_DIR.exists())
            if (!COMMAND_SAVE_DIR.mkdirs()) {
                LOG.warn("Failed to create directory for command configs");
                return null;
            }
        // Check for existing
        if (command.exists()) {
            try {
                String lines = Strings.join(Files.readAllLines(command.toPath()), '\n');
                return GSON.fromJson(lines, CommandConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                LOG.warn("Failed to load command config for '" + name + "' (" + command.getAbsolutePath() + ")");
            }
        } else {
            // Compute Cooldown Defaults
            HashMap<String, Long> cooldown = new HashMap<>();
            for (String def : moduleCommand.defaultCooldown()) {
                try {
                    String[] split = def.split(";");
                    if (split.length == 2) {
                        cooldown.put(split[0], Long.parseLong(split[1]));
                    } else
                        LOG.warn("Invalid Cooldown Format for command '" + moduleCommand.name() + "' Must be in the following format: <rank>;<time>");
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.warn("Failed to read cooldown for command '" + moduleCommand.name() + "'");
                }
            }
            // Compute Delay Defaults
            HashMap<String, Long> delay = new HashMap<>();
            for (String def : moduleCommand.defaultDelay()) {
                try {
                    String[] split = def.split(";");
                    if (split.length == 2) {
                        delay.put(split[0], Long.parseLong(split[1]));
                    } else
                        LOG.warn("Invalid Delay Format for command '" + moduleCommand.name() + "' Must be in the following format: <rank>;<time>");
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.warn("Failed to read delay for command '" + moduleCommand.name() + "'");
                }
            }
            // Compute Cost Defaults
            HashMap<String, Double> cost = new HashMap<>();
            for (String def : moduleCommand.defaultDelay()) {
                try {
                    String[] split = def.split(";");
                    if (split.length == 2) {
                        cost.put(split[0], Double.parseDouble(split[1]));
                    } else
                        LOG.warn("Invalid Cost Format for command '" + moduleCommand.name() + "' Must be in the following format: <currency>;<cost>");
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.warn("Failed to read cost for command '" + moduleCommand.name() + "'");
                }
            }
            CommandConfig config = new CommandConfig(name, moduleCommand.defaultEnabled(), "command." + name, moduleCommand.defaultMinRank(), moduleCommand.defaultSecure(), moduleCommand.defaultAliases(), cooldown, delay, cost);
            try {
                Files.write(command.toPath(), GSON.toJson(config).getBytes());
                return config;
            } catch (IOException e) {
                e.printStackTrace();
                LOG.warn("Failed to save command config for '" + name + "' (" + command.getAbsolutePath() + ")");
            }
        }
        return null;
    }

    public static List<String> predict(String current, List<String> possible) {
        if (current.isEmpty())
            return possible;
        else {
            possible.removeIf(p -> !current.toLowerCase().startsWith(current.toLowerCase()));
            return possible;
        }
    }

    public static List<String> generatePossibleAutoFill(ICommandSender sender, CommandArgument arg, String usage) {
        List<String> autofill = new ArrayList<>();
        if (arg == CommandArgument.STRING || arg == CommandArgument.STRING_ARR)
            if (usage.contains(","))
                for (String split : usage.split(","))
                    autofill.add(split.trim());
            else autofill.add(usage);
        if (arg == CommandArgument.DOUBLE)
            autofill.add(0.0 + "");
        else if (arg == CommandArgument.INTEGER)
            autofill.add(0.0 + "");
        else if (arg == CommandArgument.PLAYER)
            for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
                autofill.add(player.getDisplayNameString()); // TODO Replace Username with "Name" / nick when possible
        else if (arg == CommandArgument.RANK)
            for (Rank rank : SECore.dataLoader.getFromKey(DataLoader.DataType.RANK, new Rank()).values())
                autofill.add(rank.name);
        else if (arg == CommandArgument.HOME) {
            // TODO Autofill with user homes
        } else if (arg == CommandArgument.MODULE)
            autofill.addAll(SECore.modules.keySet());
        else if (arg == CommandArgument.CURRENCY)
            for (Currency currency : SECore.dataLoader.getFromKey(DataLoader.DataType.CURRENCY, new Currency()).values())
                autofill.add(currency.displayName);
        else if (arg == CommandArgument.WARP) {
            // TODO Autofill warps (user access only)
        } else if (arg == CommandArgument.DATA_TYPE)
            for (DataLoader.DataType type : DataLoader.DataType.values())
                autofill.add(type.name().toLowerCase());
        return autofill;
    }

}
