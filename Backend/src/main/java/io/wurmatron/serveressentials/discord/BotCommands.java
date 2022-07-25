package io.wurmatron.serveressentials.discord;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import io.wurmatron.serveressentials.utils.EncryptionUtils;
import java.time.Instant;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class BotCommands {

  // TODO Timeout
  // Username, [discordID, discordName, verifyCode, Timestamp]
  public static NonBlockingHashMap<String, String[]> verifyCodes = new NonBlockingHashMap<>();

  public static InteractionApplicationCommandCallbackReplyMono verify(
      ChatInputInteractionEvent e) {
    String username = e.getOption("username")
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asString).get();
    if (e.getInteraction().getMember().isPresent()) {
      Member member = e.getInteraction().getMember().get();
      String discordID = member.getId().asString();
      String discordName = member.getDisplayName();
      String verifyCode = EncryptionUtils.generateRandomString(8);
      LOG.info(
          "Generating Verify code '" + verifyCode + "' for user '" + username + "' via '"
              + discordName + "' (" + discordID + ")");
      verifyCodes.put(username.toUpperCase(),
          new String[]{discordID, discordName, verifyCode,
              Instant.now().getEpochSecond() + ""});
      return e.reply("Type `/verify " + verifyCode + "` in-game to verify")
          .withEphemeral(true);
    }

    return e.reply("Unable to find your discord id, please run the command on the discord servers channels.").withEphemeral(true);
  }
}
