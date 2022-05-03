/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bans")
public class Ban {

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "ip")
  private String ip;

  @Column(name = "discord_id")
  private String discordID;

  @Column(name = "banned_by")
  private String bannedBy;

  @Column(name = "ban_reason")
  private String banReason;

  @Column(name = "timestamp")
  private String timestamp;

  @Column(name = "ban_type")
  private String banType;

  @Column(name = "ban_data")
  private String banData;

  @Column(name = "ban_status")
  private String banStatus;

  public Ban() {}

  public Ban(
      String uuid,
      String ip,
      String discordID,
      String bannedBy,
      String banReason,
      String timestamp,
      String banType,
      String banData,
      String banStatus) {
    this.uuid = uuid;
    this.ip = ip;
    this.discordID = discordID;
    this.bannedBy = bannedBy;
    this.banReason = banReason;
    this.timestamp = timestamp;
    this.banType = banType;
    this.banData = banData;
    this.banStatus = banStatus;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getDiscordID() {
    return discordID;
  }

  public void setDiscordID(String discordID) {
    this.discordID = discordID;
  }

  public String getBannedBy() {
    return bannedBy;
  }

  public void setBannedBy(String bannedBy) {
    this.bannedBy = bannedBy;
  }

  public String getBanReason() {
    return banReason;
  }

  public void setBanReason(String banReason) {
    this.banReason = banReason;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getBanType() {
    return banType;
  }

  public void setBanType(String banType) {
    this.banType = banType;
  }

  public String getBanData() {
    return banData;
  }

  public void setBanData(String banData) {
    this.banData = banData;
  }

  public String getBanStatus() {
    return banStatus;
  }

  public void setBanStatus(String banStatus) {
    this.banStatus = banStatus;
  }
}
