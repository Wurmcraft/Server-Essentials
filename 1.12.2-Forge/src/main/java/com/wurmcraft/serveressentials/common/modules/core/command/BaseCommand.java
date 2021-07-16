package com.wurmcraft.serveressentials.common.modules.core.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.AnnotationLoader;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import joptsimple.internal.Strings;

import java.lang.reflect.Method;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

@ModuleCommand(name = "se", module = "Core",defaultAliases = {"Server-Essentials", "ServerEssentials"},defaultCooldown = {"*;5"})
public class BaseCommand {

    @Command(args = {CommandArgument.STRING}, usage = {"version, modules, storage"})
    public void info(ServerPlayer player, String arg) {
        if (arg.equalsIgnoreCase("version"))
            ChatHelper.send(player.sender, player.lang.COMMAND_BASE_VERSION.replaceAll("\\{@VERSION@}", ServerEssentials.VERSION));
        else if (arg.equalsIgnoreCase("modules"))
            ChatHelper.send(player.sender, player.lang.COMMAND_BASE_MODULES.replaceAll("\\{@MODULES@}", Strings.join(SECore.modules.keySet(), ", ")));
        else if (arg.equalsIgnoreCase("storage"))
            ChatHelper.send(player.sender, player.lang.COMMAND_BASE_STORAGE.replaceAll("\\{@STORAGE@}", ServerEssentials.config.storage.storageType.toUpperCase() + (ServerEssentials.config.storage.storageType.equalsIgnoreCase("Rest") ? "(" + ServerEssentials.config.storage.baseURL + ")" : "")));
    }

    @Command(args = {CommandArgument.STRING, CommandArgument.MODULE}, usage = {"reload, info", "module"})
    public void status(ServerPlayer player, String arg, String module) {
        if (arg.equalsIgnoreCase("reload") && !module.isEmpty()) {
            for (String m : SECore.modules.keySet())
                if (m.equalsIgnoreCase(module)) {
                    reloadModule(player, module, m);
                    return;
                }
        } else if(arg.equalsIgnoreCase("info")) {
            for (String m : SECore.modules.keySet())
                if (m.equalsIgnoreCase(module)) {
                    displayModuleInfo(player, m);
                    return;
                }
        }
    }

    private void reloadModule(ServerPlayer player, String module, String m) {
        Object moduleInstance = SECore.modules.get(m);
        try {
            // Reload Module Config
            if (SECore.moduleConfigs.containsKey(m)) {
                Object config = ConfigLoader.loadModuleConfig(m, AnnotationLoader.getModuleConfigInstance(m));
                if (config != null)
                    SECore.moduleConfigs.put(m.toUpperCase(), config);
            }
            // Reload Module
            Method reloadMethod = moduleInstance.getClass().getMethod(moduleInstance.getClass().getDeclaredAnnotation(Module.class).reloadMethod());
            reloadMethod.invoke(moduleInstance);
            ChatHelper.send(player.sender, player.lang.COMMAND_BASE_RELOAD.replaceAll("\\{@MODULE@}", m.toUpperCase()));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("Failed to reload module '" + module + "'");
        }
        ChatHelper.send(player.sender, player.lang.COMMAND_BASE_RELOAD_FAIL.replaceAll("\\{@MODULE@}", module.toUpperCase()));
    }

    private static void displayModuleInfo(ServerPlayer player, String name) {
        ChatHelper.send(player.sender, player.lang.SPACER);
        ChatHelper.send(player.sender, player.lang.SPACER);
    }
}
