package com.wurmcraft.serveressentials.forge.server.wrapper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.loader.CommandLoader;
import com.wurmcraft.serveressentials.forge.server.utils.AnnotationUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class SECommand {

  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    for (String name : CommandLoader.commands.keySet()) {
      Object commandInstance = CommandLoader.commands.get(name);
      ModuleCommand moduleCommand = commandInstance.getClass()
          .getAnnotation(ModuleCommand.class);
      for (Method command : AnnotationUtils
          .findAnnotationMethods(commandInstance.getClass(), Command.class)) {
        createCommand(dispatcher, commandInstance, moduleCommand,
            command.getDeclaredAnnotation(Command.class), command);
      }
    }
  }

  public static void build(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(Commands.literal("test")
        .then(Commands.literal("fire")
            .executes(ctx -> {
              ctx.getSource().sendFeedback(new StringTextComponent("Its Fire"), false);
              return 1;
            })
        )
    );
    dispatcher.register(Commands.literal("test")
        .then(Commands.literal("ice")
            .executes(ctx -> {
              ctx.getSource().sendFeedback(new StringTextComponent("Its Ice"), false);
              return 1;
            })
        )
    );
    dispatcher.register(Commands.literal("test")
        .executes(ctx -> {
          ctx.getSource().sendFeedback(new StringTextComponent("Its Nothing"), false);
          return 1;
        })
    );
    dispatcher.register(Commands.literal("test")
        .then(Commands.argument("type", StringArgumentType.string())
            .executes(ctx -> {
              ctx.getSource().sendFeedback(
                  new StringTextComponent("Its " + ctx.getArgument("type", String.class)),
                  false);
              return 1;
            })
        )
    );
  }

  public static void createCommand(CommandDispatcher<CommandSource> dispatcher,
      Object instance, ModuleCommand moduleCommand, Command command, Method method) {
    if (command.inputArguments().length == 0) {
      dispatcher.register(Commands.literal(moduleCommand.name()).executes(ctx -> {
        try {
          method.invoke(instance, ctx.getSource());
        } catch (IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
        return 1;
      }));
    }
  }
}
