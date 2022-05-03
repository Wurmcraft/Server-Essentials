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
@Table(name = "ranks", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Rank {

  @Id
  @Column(name = "name")
  private String name;

  @Column(name = "permissions")
  public String permissions;

  @Column(name = "inheritance")
  public String inheritance;

  @Column(name = "prefix")
  public String prefix;

  @Column(name = "prefix_priority")
  public int prefixPriority;

  @Column(name = "suffix")
  public String suffix;

  @Column(name = "suffix_priority")
  public int suffixPriority;

  @Column(name = "color")
  public String color;

  @Column(name = "color_priority")
  public int colorPriority;

  @Column(name = "active_servers")
  public String activeServers;

  @Column(name = "protected")
  public boolean protectedRank;

  public Rank() {}

  public Rank(
      String name,
      String permissions,
      String inheritance,
      String prefix,
      int prefixPriority,
      String suffix,
      int suffixPriority,
      String color,
      int colorPriority,
      String activeServers,
      boolean protectedRank) {
    this.name = name;
    this.permissions = permissions;
    this.inheritance = inheritance;
    this.prefix = prefix;
    this.prefixPriority = prefixPriority;
    this.suffix = suffix;
    this.suffixPriority = suffixPriority;
    this.color = color;
    this.colorPriority = colorPriority;
    this.activeServers = activeServers;
    this.protectedRank = protectedRank;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPermissions() {
    return permissions;
  }

  public void setPermissions(String permissions) {
    this.permissions = permissions;
  }

  public String getInheritance() {
    return inheritance;
  }

  public void setInheritance(String inheritance) {
    this.inheritance = inheritance;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public int getPrefixPriority() {
    return prefixPriority;
  }

  public void setPrefixPriority(int prefixPriority) {
    this.prefixPriority = prefixPriority;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public int getSuffixPriority() {
    return suffixPriority;
  }

  public void setSuffixPriority(int suffixPriority) {
    this.suffixPriority = suffixPriority;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getColorPriority() {
    return colorPriority;
  }

  public void setColorPriority(int colorPriority) {
    this.colorPriority = colorPriority;
  }

  public String getActiveServers() {
    return activeServers;
  }

  public void setActiveServers(String activeServers) {
    this.activeServers = activeServers;
  }

  public boolean isProtectedRank() {
    return protectedRank;
  }

  public void setProtectedRank(boolean protectedRank) {
    this.protectedRank = protectedRank;
  }
}
