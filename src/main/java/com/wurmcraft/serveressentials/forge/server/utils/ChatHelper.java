package com.wurmcraft.serveressentials.forge.server.utils;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.server.data.Language;
import java.util.NoSuchElementException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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
    text.getStyle().setHoverEvent(new HoverEvent(Action.SHOW_TEXT,new TextComponentString(hover.replaceAll("&", "\u00a7"))));
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
}
