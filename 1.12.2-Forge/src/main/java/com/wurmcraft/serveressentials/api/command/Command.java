package com.wurmcraft.serveressentials.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /**
     * Arguments required for the requested command
     */
    CommandArgument[] args();

    String[] usage();

    boolean isSubCommand() default false;

    String[] subCommandAliases() default "";

    boolean canConsoleUse() default false;
}
