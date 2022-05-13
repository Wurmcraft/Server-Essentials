package com.wurmcraft.serveressentials.common.data;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.loading.ModuleConfig;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;

public class AnnotationLoader {

  private static final Reflections REFLECTIONS = new Reflections("com.wurmcraft.serveressentials");

  /** Loads modules, makes sure they are valid */
  public static List<Object> loadModules() {
    Set<Class<?>> clazzes = REFLECTIONS.getTypesAnnotatedWith(Module.class);
    String[] modules = getModuleNames(clazzes);
    List<Object> loadedModules = new ArrayList<>();
    for (Class<?> clazz : clazzes)
      if (isValidModule(clazz, modules)) {
        try {
          Object instance = clazz.newInstance();
          loadedModules.add(instance);
        } catch (Exception e) {
          e.printStackTrace();
          ServerEssentials.LOG.error(
              "Failed to create an instance of module ' "
                  + clazz.getDeclaredAnnotation(Module.class).name()
                  + "'");
        }
      } else
        ServerEssentials.LOG.info(
            "Module '"
                + clazz.getDeclaredAnnotation(Module.class).name()
                + "' has not been loaded!");
    return loadedModules;
  }

  /** Load module config instances */
  public static List<Object> loadModuleConfigs() {
    Set<Class<?>> clazzes = REFLECTIONS.getTypesAnnotatedWith(ModuleConfig.class);
    List<Object> moduleConfigs = new ArrayList<>();
    for (Class<?> clazz : clazzes) {
      try {
        Object instance = clazz.newInstance();
        moduleConfigs.add(instance);
      } catch (Exception e) {
        e.printStackTrace();
        ServerEssentials.LOG.error(
            "Failed to initialize config for module '"
                + clazz.getDeclaredAnnotation(ModuleConfig.class).module()
                + "'");
      }
    }
    return moduleConfigs;
  }

  /**
   * Get the instance of a modules config based on the modules name
   *
   * @param name name of the module to attempt to find the config for
   * @return instance of the config, new instance, not loaded from config
   */
  public static Object getModuleConfigInstance(String name) {
    Set<Class<?>> clazzes = REFLECTIONS.getTypesAnnotatedWith(ModuleConfig.class);
    for (Class<?> clazz : clazzes) {
      if (clazz.getDeclaredAnnotation(ModuleConfig.class).module().equalsIgnoreCase(name)) {
        try {
          return clazz.newInstance();
        } catch (Exception e) {
          e.printStackTrace();
          ServerEssentials.LOG.error(
              "Failed to initialize config for module '"
                  + clazz.getDeclaredAnnotation(ModuleConfig.class).module()
                  + "'");
        }
      }
    }
    return null;
  }

  /**
   * Verify if a module can be loaded or not, Check name and dependencies
   *
   * @param clazz class of the module, to be checked
   * @param modules list of possible models, found from the classpath
   * @return if the module is valid or not
   */
  private static boolean isValidModule(Class<?> clazz, String[] modules) {
    Module module = clazz.getDeclaredAnnotation(Module.class);
    boolean moduleIsEnabled = false;
    if (!module.forceAlwaysLoaded()) {
      for (String enabledModule : ServerEssentials.config.enabledModules)
        if (module.name().equalsIgnoreCase(enabledModule)) {
          moduleIsEnabled = true;
          break;
        }
    } else moduleIsEnabled = true;
    return moduleIsEnabled
        && !module.name().isEmpty()
        && hasDependencies(module.dependencies(), modules);
  }

  /**
   * Check if a list of dependencies for a module exist, thus allowing the module to load
   *
   * @param dependencies the dependencies of the module to be checked
   * @param foundModules list of modules found within the classpath
   * @return if the classpath contains the required modules for this module to load
   */
  private static boolean hasDependencies(String[] dependencies, String[] foundModules) {
    if (dependencies == null
        || dependencies.length == 0
        || dependencies.length == 1 && dependencies[0].isEmpty()) return true;
    for (String module : dependencies) {
      boolean found = false;
      for (String f : foundModules)
        if (module.equalsIgnoreCase(f)) {
          found = true;
          break;
        }
      if (!found) return false;
    }
    return true;
  }

  /**
   * Collects the module names from there classes
   *
   * @param modules list of the module classes from within the classpath
   * @return list of module names from the classpath
   */
  private static String[] getModuleNames(Set<Class<?>> modules) {
    List<String> moduleNames = new ArrayList<>();
    for (Class<?> module : modules)
      moduleNames.add(module.getDeclaredAnnotation(Module.class).name());
    return moduleNames.toArray(new String[0]);
  }

  public static boolean isValidCommand(Class<?> clazz, String[] modules) {
    // Check if required module is loaded
    String requiredModule = clazz.getDeclaredAnnotation(ModuleCommand.class).module();
    boolean found = false;
    for (String module : modules) if (requiredModule.equalsIgnoreCase(module)) found = true;
    if (!found) return false;
    // Check if a method with @Command exists
    boolean hasMethod = false;
    for (Method method : clazz.getDeclaredMethods())
      if (method.isAnnotationPresent(Command.class)) hasMethod = true;
    if (!hasMethod) return false;
    return true;
  }

  public static List<Class<?>> loadCommands() {
    Set<Class<?>> clazzes = REFLECTIONS.getTypesAnnotatedWith(ModuleCommand.class);
    String[] modules = SECore.modules.keySet().toArray(new String[0]);
    List<Class<?>> loadedCommands = new ArrayList<>();
    for (Class<?> clazz : clazzes)
      if (isValidCommand(clazz, modules)) {
        loadedCommands.add(clazz);
      } else
        ServerEssentials.LOG.debug(
            "Command '"
                + clazz.getDeclaredAnnotation(ModuleCommand.class).name()
                + "' has not been loaded! requires, '"
                + clazz.getDeclaredAnnotation(ModuleCommand.class).module()
                + "'");
    return loadedCommands;
  }
}
