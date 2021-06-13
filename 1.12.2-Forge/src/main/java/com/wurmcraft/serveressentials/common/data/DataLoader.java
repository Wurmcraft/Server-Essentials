package com.wurmcraft.serveressentials.common.data;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.loading.Module;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataLoader {

    private static final Reflections REFLECTIONS = new Reflections("com.wurmcraft.serveressentials");

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
                    ServerEssentials.LOG.error("Failed to create an instance of module ' " + clazz.getDeclaredAnnotation(Module.class).name() + "'");
                }
            } else
                ServerEssentials.LOG.info("Module '" + clazz.getDeclaredAnnotation(Module.class).name() + "' has not been loaded!");
        return loadedModules;
    }

    private static boolean isValidModule(Class<?> clazz, String[] modules) {
        Module module = clazz.getDeclaredAnnotation(Module.class);
        return !module.name().isEmpty() && hasDependencies(module.dependencies(), modules);
    }

    private static boolean hasDependencies(String[] dependencies, String[] foundModules) {
        for (String module : dependencies) {
            boolean found = false;
            for (String f : foundModules)
                if (module.equalsIgnoreCase(f)) {
                    found = true;
                }
            if (!found)
                return false;
        }
        return true;
    }

    private static String[] getModuleNames(Set<Class<?>> modules) {
        List<String> moduleNames = new ArrayList<>();
        for (Class<?> module : modules)
            moduleNames.add(module.getDeclaredAnnotation(Module.class).name());
        return moduleNames.toArray(new String[0]);
    }
}
