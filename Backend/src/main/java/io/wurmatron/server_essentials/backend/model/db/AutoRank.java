/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "autoranks", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
public class AutoRank {

  @Id
  @Column(name = "title")
  private String title;

  @Column(name = "rank")
  private String rank;

  @Column(name = "next_rank")
  private String nextRank;

  @Column(name = "play_time")
  public String playTime;

  @Column(name = "currency")
  public String currency;

  @Column(name = "special_events")
  public String specialEvents;

  public AutoRank() {}

  public AutoRank(
      String title,
      String rank,
      String nextRank,
      String playTime,
      String currency,
      String specialEvents) {
    this.title = title;
    this.rank = rank;
    this.nextRank = nextRank;
    this.playTime = playTime;
    this.currency = currency;
    this.specialEvents = specialEvents;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getRank() {
    return rank;
  }

  public void setRank(String rank) {
    this.rank = rank;
  }

  public String getNextRank() {
    return nextRank;
  }

  public void setNextRank(String nextRank) {
    this.nextRank = nextRank;
  }

  public String getPlayTime() {
    return playTime;
  }

  public void setPlayTime(String playTime) {
    this.playTime = playTime;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getSpecialEvents() {
    return specialEvents;
  }

  public void setSpecialEvents(String specialEvents) {
    this.specialEvents = specialEvents;
  }
}
