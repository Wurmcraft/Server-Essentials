/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import java.util.Objects;

public class AutoRank {

  public String rank;
  public String next_rank;
  public Long play_time;
  public String currency_name;
  public Double currency_amount;
  public String special_events;

  /**
   * @param rank current rank of the user
   * @param nextRank rank the user will gain after the requirements
   * @param playtime amount of time (in minutes)
   * @param currencyName name of the currency
   * @param currencyAmount amount of the given currency
   * @param specialEvents special events required to rankup, (Used to extend functionality))
   */
  public AutoRank(
      String rank,
      String nextRank,
      long playtime,
      String currencyName,
      double currencyAmount,
      String specialEvents) {
    this.rank = rank;
    this.next_rank = nextRank;
    this.play_time = playtime;
    this.currency_name = currencyName;
    this.currency_amount = currencyAmount;
    this.special_events = specialEvents;
  }

  public AutoRank() {}

  @Override
  public String toString() {
    return ServerEssentialsRest.GSON.toJson(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AutoRank)) return false;
    AutoRank autoRank = (AutoRank) o;
    return play_time.equals(autoRank.play_time)
        && Double.compare(autoRank.currency_amount, currency_amount) == 0
        && Objects.equals(rank, autoRank.rank)
        && Objects.equals(next_rank, autoRank.next_rank)
        && Objects.equals(currency_name, autoRank.currency_name)
        && Objects.equals(special_events, autoRank.special_events);
  }

  @Override
  public AutoRank clone() {
    String json = ServerEssentialsRest.GSON.toJson(this);
    return ServerEssentialsRest.GSON.fromJson(json, AutoRank.class);
  }
}
