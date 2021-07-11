package com.wurmcraft.serveressentials.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to automatically register the provided command based on the loaded modules
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleCommand {

    /**
     * Name of the module to load this command with
     */
    String module();

    /**
     * Name of the command, for use as /<name> along with the creation  / loading of the config file
     */
    String name();
}
