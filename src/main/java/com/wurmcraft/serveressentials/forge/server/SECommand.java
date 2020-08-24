package com.wurmcraft.serveressentials.forge.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class SECommand {

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
              ctx.getSource().sendFeedback(new StringTextComponent("Its " + ctx.getArgument("type", String.class)), false);
              return 1;
            })
        )
    );
  }
}
