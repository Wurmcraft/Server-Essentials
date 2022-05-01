package io.wurmatron.server_essentials.backend.model.db;


import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
  @ElementCollection
  private List<String> ranks;

  @Column(name = "perms")
  @ElementCollection
  private List<String> additionalPerms;

  @ElementCollection
  @Column(name = "perks")
  private List<String> purchasedPerks;

  @Column(name = "language")
  private String language;

  @Column(name = "muted")
  private boolean muted;

  @Column(name = "display_name")
  private String nickname;

  @Column(name = "discord_id")
  private String discordID;

  @Column(name = "tracked_time")
  private String playTime;

  @Column(name = "wallet")
  private String bankAccounts;

  @Column(name = "password_hash")
  private String passwordHash;

  @Column(name = "password_salt")
  private String passwordSalt;

  @ElementCollection
  @Column(name = "system_perms")
  private List<String> systemPermissions;

  public UserAccount() {
  }

  public UserAccount(String uuid, String lastUsername,
      List<String> ranks, List<String> additionalPerms,
      List<String> purchasedPerks, String language, boolean muted,
      String nickname, String discordID, String playTime, String bankAccounts,
      String passwordHash, String passwordSalt,
      List<String> systemPermissions) {
    this.uuid = uuid;
    this.lastUsername = lastUsername;
    this.ranks = ranks;
    this.additionalPerms = additionalPerms;
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

  public List<String> getRanks() {
    return ranks;
  }

  public void setRanks(List<String> ranks) {
    this.ranks = ranks;
  }

  public List<String> getAdditionalPerms() {
    return additionalPerms;
  }

  public void setAdditionalPerms(List<String> additionalPerms) {
    this.additionalPerms = additionalPerms;
  }

  public List<String> getPurchasedPerks() {
    return purchasedPerks;
  }

  public void setPurchasedPerks(List<String> purchasedPerks) {
    this.purchasedPerks = purchasedPerks;
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

  public List<String> getSystemPermissions() {
    return systemPermissions;
  }

  public void setSystemPermissions(List<String> systemPermissions) {
    this.systemPermissions = systemPermissions;
  }
}
