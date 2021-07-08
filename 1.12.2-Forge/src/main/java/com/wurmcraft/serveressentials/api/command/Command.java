package com.wurmcraft.serveressentials.api.command;

public @interface Command {

    CommandArgument[] args();

    String subName() default "";
}
