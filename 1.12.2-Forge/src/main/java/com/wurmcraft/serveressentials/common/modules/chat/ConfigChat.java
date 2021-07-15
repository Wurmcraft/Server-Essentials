package com.wurmcraft.serveressentials.common.modules.chat;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Chat")
public class ConfigChat {

    public String defaultChannel;
    public String chatFormat;

    public ConfigChat(String defaultChannel, String chatFormat) {
        this.defaultChannel = defaultChannel;
        this.chatFormat = chatFormat;
    }

    public ConfigChat() {
        this.defaultChannel = "local";
        this.chatFormat = "%CHANNEL_PREFIX% %RANK_PREFIX% %NAME% %RANK_SUFFIX%: %MESSAGE%";
    }
}
