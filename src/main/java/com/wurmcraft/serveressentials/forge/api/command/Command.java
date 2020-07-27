package com.wurmcraft.serveressentials.forge.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Creates a new command in the correlation with ModuleCommand
 *
 * @see ModuleCommand
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

  /**
   * The input's that this command is required to use. The CommandArguments and the method inputs
   * must be of the same type along with an instance of the commandSender 1.12.2 -> (ICommandSender,
   * CommandArguments[]) 1.15.2 -> (CommandSource, CommandArguments[])
   *
   * @return array of arguments to use as input for this command
   */
  CommandArguments[] inputArguments();

  /**
   * Name of the linked method that this is to be extended from. This SubCommand and Command must
   * have the same Input Arguments
   *
   * @return name of the command this is a sub command off
   */
  String subCommand() default "";

  /**
   * The names for each input of the command arguments or the diff rent valid options per argument
   */
  String[] inputNames() default {};
}
