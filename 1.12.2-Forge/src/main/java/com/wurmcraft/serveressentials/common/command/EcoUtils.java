package com.wurmcraft.serveressentials.common.command;

import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.account.BankAccount;

public class EcoUtils {

    /**
     * Checks if the provided account can purchase with the provided amount
     *
     * @param currency name of the currency
     * @param amount   amount of the currency
     * @param user     account to find the currency from
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
                if (bank.currencyName.equalsIgnoreCase(currency))
                    return bank.amount;
            }
        }
        return 0.00;
    }
}
