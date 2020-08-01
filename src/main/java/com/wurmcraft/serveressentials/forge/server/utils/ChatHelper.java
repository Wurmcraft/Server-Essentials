package com.wurmcraft.serveressentials.forge.server.utils;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.server.data.Language;
import java.util.NoSuchElementException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;

public class ChatHelper {

  public static void sendMessage(ICommandSender sender, ITextComponent text) {
    sender.sendMessage(text);
  }

  public static void sendMessage(ICommandSender sender, String msg) {
    sendMessage(sender, new TextComponentString(msg.replaceAll("&", "\u00a7")));
  }

  public static void sendHoverMessage(ICommandSender sender, String msg, String hover) {
    TextComponentString text = new TextComponentString(msg.replaceAll("&", "\u00a7"));
    sendHoverMessage(sender, text, hover);
  }

  public static void sendHoverAndClickMessage(ICommandSender sender, String msg, String hover, String click) {
    TextComponentString text = new TextComponentString(msg.replaceAll("&", "\u00a7"));
    text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click));
    sendHoverMessage(sender, text, hover);
  }

  public static void sendHoverMessage(ICommandSender sender, ITextComponent text,
      String hover) {
    text.getStyle().setHoverEvent(new HoverEvent(Action.SHOW_TEXT,
        new TextComponentString(hover.replaceAll("&", "\u00a7"))));
    sendMessage(sender, text);
  }

  public static Language getDefaultLanguage() {
    try {
      return (Language) SECore.dataHandler
          .getData(DataKey.LANGUAGE, SECore.config.defaultLang);
    } catch (NoSuchElementException e) {
      try {
        String languageJson = URLUtils.readStringFromURL(
            SECore.config.langUrlBase + "/" + SECore.config.defaultLang + ".json");
        return GSON.fromJson(languageJson, Language.class);
      } catch (Exception ignored) {
        throw new NoSuchElementException();
      }
    }
  }

  public static void sendSpacerWithMessage(ICommandSender sender, String spacer,
      String msg) {
    int msgLength = calculateLength(msg);
    int leftPerSide =
        ((DefaultFontInfo.getChatLength() - msgLength) / 2) - DefaultFontInfo.SPACE
            .getLength();
    String chatMSG = spacer.substring(0, leftPerSide);
    if (chatMSG.endsWith("&")) {
      chatMSG = chatMSG.substring(0, chatMSG.length() - 1);
    }
    chatMSG = chatMSG + " " + msg + " " + spacer.substring(0, leftPerSide);
    sendMessage(sender, chatMSG);
  }

  private static int calculateLength(String msg) {
    int total = 0;
    for (int index = 0; index < msg.toCharArray().length; index++) {
      char currentChar = msg.toCharArray()[index];
      if (currentChar == '&') {
        index += 2;
      } else {
        total += DefaultFontInfo.getDefaultFontInfo(currentChar).getLength();
      }
    }
    return total;
  }
}
