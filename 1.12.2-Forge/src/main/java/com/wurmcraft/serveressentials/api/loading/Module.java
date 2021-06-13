package com.wurmcraft.serveressentials.api.loading;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to create a new module
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {

    /**
     * Name of the module, to be used in the config, in-game
     */
    String name();

    /**
     * Force the module to always be loaded, even if its not included in the module list
     */
    boolean forceAlwaysLoaded() default false;

    /**
     * A array of modules that are required for this module to be loaded
     */
    String[] dependencies() default "";

    /**
     * Method to find via reflection to setup the module
     */
    String setupMethod() default "setup";

    /**
     * Method to find via reflection to reload the module
     */
    String reloadMethod() default "reload";

}
