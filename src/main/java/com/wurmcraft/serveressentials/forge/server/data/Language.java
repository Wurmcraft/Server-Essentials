package com.wurmcraft.serveressentials.forge.server.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class Language implements JsonParser {

  public String key;

  // Announcements
  public String ANNOUNCEMENT_NEW_PLAYER;
  public String ANNOUNCEMENT_AUTORANK;

  // Hover Info
  public String HOVER_PLAYER_NAME;

  // Command
  public String COMMAND_USAGE;
  public String COMMAND_SPACER;
  public String COMMAND_COOLDOWN;
  public String COMMAND_PLAYER_NONE;
  public String COMMAND_NUMBER;

  // Core
  public String CORE_SE_RELOAD;
  public String CORE_SE_MODULES;
  public String CORE_SE_VERSION;
  public String CORE_SE_VERSION_REST;
  public String CORE_SE_STORAGE;

  // Rank
  public String RANK_CHANGE;
  public String RANK_CHANGE_SENDER;
  public String RANK_PREFIX;
  public String RANK_SUFFIX;
  public String RANK_PERM_ADD;
  public String RANK_PERM_DEL;
  public String RANK_INHERIT_ADD;
  public String RANK_INHERIT_DEL;
  public String RANK_CREATE;
  public String RANK_DELETE;

  // AutoRank
  public String AUTORANK_AR_NEXT;
  public String AUTORANK_AR_TIME;
  public String AUTORANK_AR_XP;
  public String AUTORANK_AR_MONEY;
  public String AUTORANK_AR_MAX;
  public String AUTORANK_AR_CREATE;
  public String AUTORANK_AR_DELETE;

  // General
  public String GENERAL_SETHOME_CREATED;
  public String GENERAL_SETHOME_UPDATED;
  public String GENERAL_SETHOME_MAX;
  public String GENERAL_SETHOME_INVALID;
  public String GENERAL_DELHOME_HOME;
  public String GENERAL_DELHOME_ALL;
  public String GENERAL_HOME;
  public String GENERAL_TPA_SENT;
  public String GENERAL_TPA_REQUEST;
  public String GENERAL_TPACCEPT_NONE;
  public String GENERAL_TPA;
  public String GENERAL_BACK;
  public String GENERAL_GAMEMODE_SURVIVAL;
  public String GENERAL_GAMEMODE_CREATIVE;
  public String GENERAL_GAMEMODE_ADVENTURE;
  public String GENERAL_GAMEMODE_SPECTATOR;
  public String GENERAL_GAMEMODE_INVAID;
  public String GENERAL_GAMEMODE_SURVIVAL_OTHER;
  public String GENERAL_GAMEMODE_CREATIVE_OTHER;
  public String GENERAL_GAMEMODE_ADVENTURE_OTHER;
  public String GENERAL_GAMEMODE_SPECTATOR_OTHER;
  public String GENERAL_FEED_OTHER;
  public String GENERAL_FEED;
  public String GENERAL_HEAL;
  public String GENERAL_HEAL_OTHER;
  public String GENERAL_FLYE_OTHER;
  public String GENERAL_FLYD_OTHER;
  public String GENERAL_FLYE;
  public String GENERAL_FLYD;
  public String GENERAL_GODE_OTHER;
  public String GENERAL_GODD_OTHER;
  public String GENERAL_GODE;
  public String GENERAL_GODD;
  public String GENERAL_HAT;
  public String GENERAL_PING;
  public String GENERAL_RAIN;
  public String GENERAL_THUNDER;
  public String  GENERAL_SUN;
  public String GENERAL_SUDO;
  public String GENERAL_RENAME;
  public String GENERAL_SKULL;
  public String GENERAL_SMITE;
  public String GENERAL_SMITE_OTHER;
  public String GENERAL_SPEED;
  public String GENERAL_SPEED_WALK;
  public String GENERAL_SPEED_FLY;
  public String GENERAL_SPEED_OTHER;
  public String GENERAL_SPEED_FLY_OTHER;
  public String GENERAL_SPEED_WALK_OTHER;
  public String GENERAL_UUID;
  public String GENERAL_LASTSEEN;
  public String GENERAL_VANISH;
  public String GENERAL_VANISH_UNDO;
  public String GENERAL_FREEZE;
  public String  GENERAL_FREEZE_OTHER;
  public String GENERAL_FREEZE_UNDO;
  public String GENERAL_FREEZE_UNDO_OTHER;
  public String GENERAL_DPF;
  public String GENERAL_BURN_OTHER;
  public String GENERAL_TPALL;
  public String GENERAL_TP_CORDS;
  public String GENERAL_TP_CORDS_OTHER;
  public String GENERAL_TP_PLAYER_PLAYER;
  public String GENERAL_TP_PLAYER;
  public String GENERAL_MOTD_SET;
  public String GENERAL_RULES_SET;
  public String GENERAL_RANDOMMSG_SET;
  public String GENERAL_ONLINETIME;
  public String GENERAL_TPHERE;
  public String GENERAL_TPAHERE_REQUEST;
  public String GENERAL_TPAHERE_SENT;
  public String GENERAL_WARP;
  public String GENERAL_SETWARP;
  public String GENERAL_DELWARP;
  public String GENERAL_TOP;
  public String GENERAL_JUMP;
  public String GENERAL_TPLOCK_ENABLE;
  public String GENERAL_TPLOCK_DISABLE;
  public String GENERAL_TPLOCK_NOTIFY;
  public String GENERAL_SENDTOSPAWN;
  public String GENERAL_DPF_DISCONNECT;
  public String GENERAL_KICK;
  public String GENERAL_KIT_CREATED;
  public String GENERAL_KIT_NONE;
  public String GENERAL_KIT;

  // Economy
  public String SIGN_NO_ITEM;
  public String ECO_MONEY_INSUFFICENT;
  public String SIGN_BUY;
  public String SIGN_INVENTORY_FULL;
  public String ECO_BAL;
  public String ECO_PAY_SENT;
  public String ECO_PAY_GIVEN;
  public String ECO_ADD;
  public String ECO_DEL;
  public String GENERAL_SPAWN;
  public String GENERAL_SETSPAWN;
  public String GENERAL_SPAWN_NONE;
  public String COMMAND_MOVE;
  public String COMMAND_MOVED;
  public String SIGN_SELL;
  public String SIGN_SELL_EMPTY;
  public String SIGN_EMPTY;

  // Language
  public String LANGUAGE_MUTED;
  public String LANGUAGE_SPAM;
  public String LANGUAGE_MUTE;
  public String LANGUAGE_MUTE_UNDO;
  public String LANGUAGE_REPLY_NONE;
  public String LANGUAGE_SOCIALSPY_DISABLED;
  public String LANGUAGE_SOCIALSPY_ENABLED;
  public String LANGUAGE_CHANGE;
  public String LANGUAGE_INVALID;
  public String LANGUAGE_CHANNEL_NONE;
  public String LANGUAGE_CHANNEL;

  // Discord
  public String DISCORD_VERIFIED;
  public String DISCORD_INVALID_CODE;

  // Security
  public String SECURITY_LOCKDOWN_ENABLED;
  public String SECURITY_LOCKDOWN_DISABLED;
  public String SECURITY_TRUSTED;
  public String SECURITY_ALT;
  public String SECURITY_BLACKLIST;
  public String SECURITY_BLACKLIST_CONNECT;

  // Ban
  public String BAN_UNBAN;

  // Protect
  public String PROTECT_STOP;
  public String PROTECT_START;
  public String PROTECT_TOO_SMALL;
  public String PROTECT_CREATED;
  public String PROTECT_TRUST_FAIL;
  public String PROTECT_TRUST;

  public Language(String key) {
    this.key = key;
  }

  @Override
  public String getID() {
    return key;
  }
}
