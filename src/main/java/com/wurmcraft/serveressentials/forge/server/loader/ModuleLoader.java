package com.wurmcraft.serveressentials.forge.server.loader;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.utils.AnnotationUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class ModuleLoader {

  public static final NonBlockingHashMap<String, Object> modules = new NonBlockingHashMap<>();
  private static boolean allowSysOut = true;

  private static void loadModules()
      throws IllegalAccessException, InstantiationException {
    Set<Class<?>> moduleClasses = AnnotationUtils.findAnnotation(Module.class);
    for (Class<?> module : moduleClasses) {
      if (canModuleBeLoaded(module.newInstance(), module.getAnnotation(Module.class))) {
        Module moduleAnnotation = module.getAnnotation(Module.class);
        ServerEssentialsServer.LOGGER.info("Loading Module '" + moduleAnnotation.name() + "'");
        modules.put(moduleAnnotation.name().toUpperCase(), module.newInstance());
        if (SECore.config.debug) {
          ServerEssentialsServer.LOGGER.trace(
              "Module[" + moduleAnnotation.name() + ":" + Arrays
                  .toString(moduleAnnotation.moduleDependencies()) + "] (" + module
                  .getName() + ")");
        }
      }
    }
  }

  public static Object getLoadedModule(String name) {
    return modules.getOrDefault(name.toUpperCase(), null);
  }

  public static void reloadModule(String name) {
    allowSysOut = false;
    Object moduleInstance = getModuleFromName(name);
    if (moduleInstance != null) {
      runModuleMethod(moduleInstance, "Reload");
    } else {
      ServerEssentialsServer.LOGGER.warn("Tried reloading Module '" + name + "' ho");
    }
    allowSysOut = true;
  }

  public static void setupModule() {
    if (modules.size() == 0) {
      try {
        loadModules();
      } catch (Exception e) {
        e.printStackTrace();
        ServerEssentialsServer.LOGGER.error("Error Loading Modules");
      }
    }
    ServerEssentialsServer.LOGGER
        .info("Setting up modules " + Arrays.toString(modules.keySet().toArray()));
    modules.values().forEach(m -> runModuleMethod(m, "Init"));
    modules.values().forEach(m -> runModuleMethod(m, "Finalize"));
  }

  private static void runModuleMethod(Object moduleInstance, String type) {
    Module module = moduleInstance.getClass().getAnnotation(Module.class);
    if (SECore.config.debug) {
      ServerEssentialsServer.LOGGER.info( type + " Module '" + module.name() + "'");
    }
    try {
      Method method = null;
      if (type.equalsIgnoreCase("init")) {
        method = moduleInstance.getClass().getMethod(module.initializeMethod());
      } else if (type.equalsIgnoreCase("finalize")) {
        method = moduleInstance.getClass().getMethod(module.completeSetup());
      } else if (type.equalsIgnoreCase("reload")) {
        method = moduleInstance.getClass().getMethod(module.reloadModule());
      }
      method.invoke(moduleInstance);
    } catch (Exception e) {
      ServerEssentialsServer.LOGGER.error("Module '" + module.name()
          + "' has loaded incorrectly, removing from module list");
      modules.remove(module.name().toUpperCase());
    }
  }

  private static Object getModuleFromName(String name) {
    if (!modules.isEmpty() && modules.containsKey(name.toUpperCase())) {
      return modules.get(name.toUpperCase());
    }
    Set<Class<?>> moduleClasses = AnnotationUtils.findAnnotation(Module.class);
    for (Class<?> module : moduleClasses) {
      if (module.getAnnotation(Module.class).name().equalsIgnoreCase(name)) {
        try {
          return module.newInstance();
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
    }
    return null;
  }

  private static boolean canModuleBeLoaded(Object instance, Module module) {
    if (hasValidStructure(instance, module) &&
        (hasModuleInConfig(module) || module.shouldAllaysBeLoaded())) {
      ServerEssentialsServer.LOGGER.info(
          "Checking module '" + module.name() + " dependencies" + " " + hasDependencies(
              module));
      if (hasDependencies(module)) {
        return true;
      } else {
        if (allowSysOut) {
          ServerEssentialsServer.LOGGER.error("Failed to load module '" + module.name()
              + "' because it was missing one ore more of its dependencies!");
          ServerEssentialsServer.LOGGER.error(
              "Module '" + module.name() + "' requires " + Arrays
                  .asList(module.moduleDependencies()) + " to be loaded!");
        }
      }
    }
    if (SECore.config.debug && allowSysOut) {
      ServerEssentialsServer.LOGGER
          .error("Module '" + module.name() + "' was found but not loaded!");
    }
    return false;
  }

  private static boolean hasModuleInConfig(Module module) {
    return Arrays.stream(SECore.config.modules)
        .anyMatch(configVal -> configVal.equalsIgnoreCase(module.name()));
  }

  private static boolean hasValidStructure(Object instance, Module module) {
    try {
      instance.getClass().getDeclaredMethod(module.initializeMethod());
      instance.getClass().getDeclaredMethod(module.completeSetup());
      instance.getClass().getDeclaredMethod(module.reloadModule());
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private static boolean hasDependencies(Module module) {
    if (module.moduleDependencies().length > 0) {
      if (module.moduleDependencies().length == 1 && module.moduleDependencies()[0]
          .isEmpty()) {
        return true;
      }
      for (String dependency : module.moduleDependencies()) {
        if (dependency.isEmpty()) {
          continue;
        }
        allowSysOut = false;
        Object depend = getModuleFromName(dependency);
        if (depend == null) {
          return false;
        }
        if (!canModuleBeLoaded(depend, depend.getClass().getAnnotation(Module.class))) {
          return false;
        }
      }
      allowSysOut = true;
    }
    return true;
  }
}