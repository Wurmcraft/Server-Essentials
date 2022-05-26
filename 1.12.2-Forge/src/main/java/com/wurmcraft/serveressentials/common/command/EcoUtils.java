package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.account.BankAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy.Taxes;

public class EcoUtils {

  /**
   * Checks if the provided account can purchase with the provided amount
   *
   * @param currency name of the currency
   * @param amount amount of the currency
   * @param user account to find the currency from
   */
  public static boolean canBuy(String currency, double amount, Account user) {
    if (user != null && user.wallet != null)
      for (BankAccount account : user.wallet) {
        if (account.currencyName.equalsIgnoreCase(currency)) {
          return account.amount >= amount;
        }
      }
    return false;
  }

  public static double balance(Account account, String currency) {
    if (account.wallet != null && account.wallet.length > 0) {
      for (BankAccount bank : account.wallet) {
        if (bank.currencyName.equalsIgnoreCase(currency)) return bank.amount;
      }
    }
    return 0.00;
  }

  public static void buy(Account account, String currency, double amount) {
      for(BankAccount bank : account.wallet)
        if(bank.currencyName.equals(currency)) {
          bank.amount = bank.amount - amount;
          SECore.dataLoader.update(DataType.ACCOUNT,account.uuid, account);
          return;
        }
  }

  public static void earn(Account account, String currency, double amount) {
    for(BankAccount bank : account.wallet)
      if(bank.currencyName.equals(currency)) {
        bank.amount = bank.amount + amount;
        SECore.dataLoader.update(DataType.ACCOUNT,account.uuid, account);
        return;
      }
  }

  public static double computeTax(double amount) {
    Taxes taxes = ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).taxes;
    if(taxes.payTaxMultiplayer > 0) {
      return amount + (amount * taxes.payTaxMultiplayer);
    }
    return amount;
  }
}
