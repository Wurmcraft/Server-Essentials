package com.wurmcraft.serveressentials.fabric.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create a command for a given module and registers it with the module and minecraft automatically
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleCommand {

  /**
   * Sets the module this command will be registered under, if no module name is given it will
   * attempt to get find one based on the package the class is located in.
   *
   * @return name of the module to register this command too
   */
  String moduleName() default "";

  /** @return name of the command used via /<name> */
  String name();

  /** Alternative Names for using this command */
  String[] aliases() default {};
}