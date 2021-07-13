package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.api.command.CommandConfig;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;
import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

public class CommandUtils {

    public static File COMMAND_SAVE_DIR = new File(SAVE_DIR + File.separator + "Commands");

    public static CommandConfig loadConfig(String name) {
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
            name = name.toLowerCase();
            // TODO Get defaults from @ModuleCommand
            CommandConfig config = new CommandConfig(name, true, "command." + name, "", false, new String[0], new HashMap<>(), new HashMap<>(), new HashMap<>());
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

}
