package com.wurmcraft.serveressentials.forge.server.loader;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.utils.AnnotationUtils;
import java.lang.reflect.Method;
import java.util.Set;
import org.cliffc.high_scale_lib.NonBlockingHashMap;


public class CommandLoader {

  public static final NonBlockingHashMap<String, Object> commands = new NonBlockingHashMap<>();

  private static void loadCommands()
      throws IllegalAccessException, InstantiationException {
    ServerEssentialsServer.LOGGER.info("Searching for Commands");
    Set<Class<?>> commandClasses = AnnotationUtils.findAnnotation(ModuleCommand.class);
    ServerEssentialsServer.LOGGER.info("Found " + commandClasses.size() + " Command(s)!");
    for (Class<?> command : commandClasses) {
      if (canCommandBeLoaded(command, command.getAnnotation(ModuleCommand.class))) {
        ModuleCommand moduleCommand = command.getAnnotation(ModuleCommand.class);
        commands.put(moduleCommand.name(), command.newInstance());
        if (SECore.config.debug) {
          ServerEssentialsServer.LOGGER.info(
              "Loading command '" + moduleCommand.moduleName() + ":" + moduleCommand
                  .name() + "' (" + command.getSimpleName() + ") (" + AnnotationUtils
                  .findAnnotationMethods(command, Command.class).size() + ")");
        }
      }
    }
  }

  public static void setupCommands() {
    try {
      loadCommands();
    } catch (Exception e) {
      ServerEssentialsServer.LOGGER.info(e.getMessage());
    }
  }

  private static boolean canCommandBeLoaded(Class<?> clazz, ModuleCommand command) {
    Set<Method> commandPaths = AnnotationUtils
        .findAnnotationMethods(clazz, Command.class);
    return !commandPaths.isEmpty()
        && ModuleLoader.getLoadedModule(command.moduleName()) != null;
  }
}
