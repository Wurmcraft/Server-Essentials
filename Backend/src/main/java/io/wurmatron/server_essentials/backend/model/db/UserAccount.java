package io.wurmatron.server_essentials.backend.model.db;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"uuid", "discord_id"}))
public class UserAccount {

  @Id
  @Column(name = "uuid")
  private String uuid;

  @Column(name = "username")
  private String lastUsername;

  @Column(name = "ranks")
  private String ranks;

  @Column(name = "perms")
  private String perms;

  @Column(name = "perks")
  private String purchasedPerks;

  @Column(name = "language")
  private String language;

  @Column(name = "muted")
  private boolean muted;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "discord_id")
  private String discordID;

  @Column(name = "playtime")
  private String playTime;

  @Column(name = "wallet")
  private String bankAccounts;

  @Column(name = "password_hash")
  private String passwordHash;

  @Column(name = "password_salt")
  private String passwordSalt;

  @Column(name = "system_perms")
  private String systemPermissions;

  public UserAccount() {
  }

  public UserAccount(String uuid, String lastUsername, String ranks, String perms,
      String purchasedPerks, String language, boolean muted, String nickname,
      String discordID, String playTime, String bankAccounts, String passwordHash,
      String passwordSalt, String systemPermissions) {
    this.uuid = uuid;
    this.lastUsername = lastUsername;
    this.ranks = ranks;
    this.perms = perms;
    this.purchasedPerks = purchasedPerks;
    this.language = language;
    this.muted = muted;
    this.nickname = nickname;
    this.discordID = discordID;
    this.playTime = playTime;
    this.bankAccounts = bankAccounts;
    this.passwordHash = passwordHash;
    this.passwordSalt = passwordSalt;
    this.systemPermissions = systemPermissions;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getLastUsername() {
    return lastUsername;
  }

  public void setLastUsername(String lastUsername) {
    this.lastUsername = lastUsername;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public boolean isMuted() {
    return muted;
  }

  public void setMuted(boolean muted) {
    this.muted = muted;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getDiscordID() {
    return discordID;
  }

  public void setDiscordID(String discordID) {
    this.discordID = discordID;
  }

  public String getPlayTime() {
    return playTime;
  }

  public void setPlayTime(String playTime) {
    this.playTime = playTime;
  }

  public String getBankAccounts() {
    return bankAccounts;
  }

  public void setBankAccounts(String bankAccounts) {
    this.bankAccounts = bankAccounts;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getPasswordSalt() {
    return passwordSalt;
  }

  public void setPasswordSalt(String passwordSalt) {
    this.passwordSalt = passwordSalt;
  }

  public String getSystemPermissions() {
    return systemPermissions;
  }

  public void setSystemPermissions(String systemPermissions) {
    this.systemPermissions = systemPermissions;
  }

  public String getRanks() {
    return ranks;
  }

  public void setRanks(String ranks) {
    this.ranks = ranks;
  }

  public String getPerms() {
    return perms;
  }

  public void setPerms(String perms) {
    this.perms = perms;
  }

  public String getPurchasedPerks() {
    return purchasedPerks;
  }

  public void setPurchasedPerks(String purchasedPerks) {
    this.purchasedPerks = purchasedPerks;
  }


}
