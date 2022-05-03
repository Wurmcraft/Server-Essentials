/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "currencies", uniqueConstraints = @UniqueConstraint(columnNames = {"display_name"}))
public class Currency {

  @Column(name = "display_name")
  public String displayName;

  @Column(name = "global_worth")
  private String globalWorth;

  @Column(name = "sell_worth")
  private String sellWorth;

  @Column(name = "tax")
  private String tax;

  public Currency() {}

  public Currency(String displayName, String globalWorth, String sellWorth, String tax) {
    this.displayName = displayName;
    this.globalWorth = globalWorth;
    this.sellWorth = sellWorth;
    this.tax = tax;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getGlobalWorth() {
    return globalWorth;
  }

  public void setGlobalWorth(String globalWorth) {
    this.globalWorth = globalWorth;
  }

  public String getSellWorth() {
    return sellWorth;
  }

  public void setSellWorth(String sellWorth) {
    this.sellWorth = sellWorth;
  }

  public String getTax() {
    return tax;
  }

  public void setTax(String tax) {
    this.tax = tax;
  }
}
