/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import java.util.Arrays;
import java.util.Objects;

public class Rank {

  public String name;
  public String[] permissions;
  public String[] inheritance;
  public String prefix;
  public Integer prefix_priority;
  public String suffix;
  public Integer suffix_priority;
  public String color;
  public Integer color_priority;

  /**
   * @param name name of the given rank
   * @param permissions permissions this rank has been given
   * @param inheritance inheritance, "rank ladder"
   * @param prefix prefix for this rank
   * @param prefixPriority priority of this rank's prefix, default 0
   * @param suffix suffix for this rank
   * @param suffixPriority suffix for this rank's suffix, default 0
   * @param color color to used for displaying this rank
   * @param colorPriority priority of this color, default 0
   */
  public Rank(
      String name,
      String[] permissions,
      String[] inheritance,
      String prefix,
      int prefixPriority,
      String suffix,
      int suffixPriority,
      String color,
      int colorPriority) {
    this.name = name;
    this.permissions = permissions;
    this.inheritance = inheritance;
    this.prefix = prefix;
    this.prefix_priority = prefixPriority;
    this.suffix = suffix;
    this.suffix_priority = suffixPriority;
    this.color = color;
    this.color_priority = colorPriority;
  }

  public Rank() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Rank)) {
      return false;
    }
    Rank rank = (Rank) o;
    return prefix_priority.equals(rank.prefix_priority)
        && suffix_priority.equals(rank.suffix_priority)
        && color_priority.equals(rank.color_priority)
        && name.equalsIgnoreCase(rank.name)
        && Arrays.equals(permissions, rank.permissions)
        && Arrays.equals(inheritance, rank.inheritance)
        && Objects.equals(prefix, rank.prefix)
        && Objects.equals(suffix, rank.suffix)
        && Objects.equals(color, rank.color);
  }

  @Override
  public Rank clone() {
    String json = ServerEssentialsRest.GSON.toJson(this);
    return ServerEssentialsRest.GSON.fromJson(json, Rank.class);
  }
}
