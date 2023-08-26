/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package com.wurmcraft.serveressentials.api.models.account;

import java.util.Objects;

public class BankAccount {

  public String currencyName;
  public double amount;
  public long lastUsed;
  public String accountType;
  public String accountData;
  public long lastCalculated;

  /**
   * @param currencyName name of the currency
   * @param amount amount of the given currency
   * @param lastUsed last time the currency was used
   * @param accountType type of account
   * @param accountData data related to the account
   * @param lastCalculated time the account perks where calculated
   */
  public BankAccount(
      String currencyName,
      double amount,
      long lastUsed,
      String accountType,
      String accountData,
      long lastCalculated) {
    this.currencyName = currencyName;
    this.amount = amount;
    this.lastUsed = lastUsed;
    this.accountType = accountType;
    this.accountData = accountData;
    this.lastCalculated = lastCalculated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BankAccount)) {
      return false;
    }
    BankAccount that = (BankAccount) o;
    return Double.compare(that.amount, amount) == 0
        && lastUsed == that.lastUsed
        && lastCalculated == that.lastCalculated
        && currencyName.equals(that.currencyName)
        && accountType.equals(that.accountType)
        && Objects.equals(accountData, that.accountData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyName, amount, lastUsed, accountType, accountData,
        lastCalculated);
  }
}
