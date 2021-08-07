package com.wurmcraft.serveressentials.common.modules.chat;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Chat")
public class ConfigChat {

    public String defaultChannel;
    public String chatFormat;
    public String nickFormat;
    public String defaultMuteDuration;

    public ConfigChat(String defaultChannel, String chatFormat, String nickFormat, String defaultMuteDuration) {
        this.defaultChannel = defaultChannel;
        this.chatFormat = chatFormat;
        this.nickFormat = nickFormat;
        this.defaultMuteDuration = defaultMuteDuration;
    }

    public ConfigChat() {
        this.defaultChannel = "local";
        this.chatFormat = "%CHANNEL_PREFIX% %RANK_PREFIX% %NAME% %RANK_SUFFIX%: %MESSAGE%";
        this.defaultMuteDuration = "10m";
        this.nickFormat = "*%NICK%";
    }
}
