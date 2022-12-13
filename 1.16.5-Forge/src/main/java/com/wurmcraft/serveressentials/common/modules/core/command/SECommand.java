package com.wurmcraft.serveressentials.common.modules.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.impl.SayCommand;
import net.minecraft.util.text.StringTextComponent;

public class SECommand {

  protected static class CommandArgument implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("modules", "version",
        "reload");

    public CommandArgument() {
    }

    public static CommandArgument arg() {
      return new CommandArgument();
    }

    public String parse(StringReader reader) throws CommandSyntaxException {
      return reader.readString();
    }


    public <S> CompletableFuture<Suggestions> listSuggestions(
        CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (p_listSuggestions_1_.getSource() instanceof ISuggestionProvider) {
        return ISuggestionProvider.suggest(EXAMPLES, p_listSuggestions_2_);
      } else {
        return Suggestions.empty();
      }
    }

    public Collection<String> getExamples() {
      return EXAMPLES;
    }

    public static String get(CommandContext<CommandSource> sender, String name) {
      return sender.getArgument(name, String.class);
    }
  }

  public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
    LiteralCommandNode<CommandSource> literalcommandnode = commandDispatcher.register(
        Commands.literal("serveressentials").requires((p_198816_0_) -> {
          return p_198816_0_.hasPermission(2);
        }).then(Commands.argument("arg", CommandArgument.arg()).executes((sender) -> {
          sender.getSource().sendSuccess(
              new StringTextComponent("Arg: " + CommandArgument.get(sender, "arg")),
              true);
          return 1;
        })));
    commandDispatcher.register(Commands.literal("se").requires((p_200556_0_) -> {
      return p_200556_0_.hasPermission(2);
    }).redirect(literalcommandnode));
  }
}
