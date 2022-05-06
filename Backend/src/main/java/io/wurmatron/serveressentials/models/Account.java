/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.account.BankAccount;
import io.wurmatron.serveressentials.models.account.ServerTime;
import java.util.Arrays;
import java.util.Objects;

public class Account {

  public String uuid;
  public String username;
  public String[] rank;
  public String[] perms;
  public String[] perks;
  public String lang;
  public Boolean muted;
  public Long mute_time;
  public String display_name;
  public String discord_id;
  public ServerTime[] tracked_time;
  public BankAccount[] wallet;
  public Integer reward_points;
  public String password_hash;
  public String password_salt;
  public String[] system_perms;

  /**
   * @param uuid uuid of the given user
   * @param username username of the given user
   * @param rank ranks of the given user
   * @param perms extra perms of the given user
   * @param perks extra perms of the given user
   * @param language language of the given user
   * @param muted is the user muted
   * @param muteTime How long until the user will be unmute'd
   * @param displayName user's displayName, "Nickname"
   * @param discordID user's discordID
   * @param trackedTime list of instances of the user's playtime
   * @param wallet list of bank accounts
   * @param rewardPoints points the user has earned
   * @param passwordHash hash of the user's system password
   * @param passwordSalt salt of the user's password
   * @param systemPerms permissions to interact with the system
   */
  public Account(
      String uuid,
      String username,
      String[] rank,
      String[] perms,
      String[] perks,
      String language,
      boolean muted,
      long muteTime,
      String displayName,
      String discordID,
      ServerTime[] trackedTime,
      BankAccount[] wallet,
      int rewardPoints,
      String passwordHash,
      String passwordSalt,
      String[] systemPerms) {
    this.uuid = uuid;
    this.username = username;
    this.rank = rank;
    this.perms = perms;
    this.perks = perks;
    this.lang = language;
    this.muted = muted;
    this.mute_time = muteTime;
    this.display_name = displayName;
    this.discord_id = discordID;
    this.tracked_time = trackedTime;
    this.wallet = wallet;
    this.reward_points = rewardPoints;
    this.password_hash = passwordHash;
    this.password_salt = passwordSalt;
    this.system_perms = systemPerms;
  }

  public Account() {}

  @Override
  public Account clone() {
    String json = ServerEssentialsRest.GSON.toJson(this);
    return ServerEssentialsRest.GSON.fromJson(json, Account.class);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Account)) return false;
    Account account = (Account) o;
    return Objects.equals(uuid, account.uuid)
        && Objects.equals(username, account.username)
        && Arrays.equals(rank, account.rank)
        && Arrays.equals(perms, account.perms)
        && Arrays.equals(perks, account.perks)
        && Objects.equals(lang, account.lang)
        && Objects.equals(muted, account.muted)
        && Objects.equals(mute_time, account.mute_time)
        && Objects.equals(display_name, account.display_name)
        && Objects.equals(discord_id, account.discord_id)
        && Arrays.equals(tracked_time, account.tracked_time)
        && Arrays.equals(wallet, account.wallet)
        && Objects.equals(reward_points, account.reward_points)
        && Objects.equals(password_hash, account.password_hash)
        && Objects.equals(password_salt, account.password_salt); // &&
    //                Arrays.equals(system_perms, account.system_perms);
  }
}
