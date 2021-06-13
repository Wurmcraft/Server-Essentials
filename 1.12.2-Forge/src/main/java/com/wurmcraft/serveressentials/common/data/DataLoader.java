package com.wurmcraft.serveressentials.common.data;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.loading.Module;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataLoader {

    private static final Reflections REFLECTIONS = new Reflections("com.wurmcraft.serveressentials");

    /**
     * Loads modules, makes sure they are valid
     */
    public static List<Object> loadModules() {
        Set<Class<?>> clazzes = REFLECTIONS.getTypesAnnotatedWith(Module.class);
        String[] modules = getModuleNames(clazzes);
        List<Object> loadedModules = new ArrayList<>();
        for (Class<?> clazz : clazzes)
            if (isValidModule(clazz, modules)) {
                try {
                    // TODO Check config to load
                    Object instance = clazz.newInstance();
                    loadedModules.add(instance);
                } catch (Exception e) {
                    e.printStackTrace();
                    ServerEssentials.LOG.error("Failed to create an instance of module ' " + clazz.getDeclaredAnnotation(Module.class).name() + "'");
                }
            } else
                ServerEssentials.LOG.info("Module '" + clazz.getDeclaredAnnotation(Module.class).name() + "' has not been loaded!");
        return loadedModules;
    }

    /**
     * Verify if a module can be loaded or not, Check name and dependencies
     *
     * @param clazz   class of the module, to be checked
     * @param modules list of possible models, found from the classpath
     * @return if the module is valid or not
     */
    private static boolean isValidModule(Class<?> clazz, String[] modules) {
        Module module = clazz.getDeclaredAnnotation(Module.class);
        return !module.name().isEmpty() && hasDependencies(module.dependencies(), modules);
    }

    /**
     * Check if a list of dependencies for a module exist, thus allowing the module to load
     *
     * @param dependencies the dependencies of the module to be checked
     * @param foundModules list of modules found within the classpath
     * @return if the classpath contains the required modules for this module to load
     */
    private static boolean hasDependencies(String[] dependencies, String[] foundModules) {
        if (dependencies == null || dependencies.length == 0 || dependencies.length == 1 && dependencies[0].isEmpty())
            return true;
        for (String module : dependencies) {
            boolean found = false;
            for (String f : foundModules)
                if (module.equalsIgnoreCase(f)) {
                    found = true;
                    break;
                }
            if (!found)
                return false;
        }
        return true;
    }

    /**
     * Collects the module  names from there classes
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
}
