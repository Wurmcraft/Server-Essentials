package com.wurmcraft.serveressentials.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Used to automatically register the provided command based on the loaded modules */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleCommand {

  /** Name of the module to load this command with */
  String module();

  /**
   * Name of the command, for use as /<name> along with the creation / loading of the config file
   */
  String name();

  /** Possible aliases for using this command */
  String[] defaultAliases() default "";

  /** Is this command enabled */
  boolean defaultEnabled() default true;

  /** Minimum rank required to run this command */
  String defaultMinRank() default "";

  /** This command requires the user to be a secure user */
  boolean defaultSecure() default false;

  /** How long it takes to run this command again Format: <name>;<cost/time> */
  String[] defaultCooldown() default "";

  /** How long it takes to run this command (startup) Format: <name>;<cost/time> */
  String[] defaultDelay() default "";

  /** How long it takes to run this command (startup Format: <name>;<cost/time> */
  String[] defaultCost() default "";
}
