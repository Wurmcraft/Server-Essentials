package io.wurmatron.serveressentials.models.account;

public class BankAccount {

    public String currencyName;
    public double amount;
    public long lastUsed;
    public String accountType;
    public String accountData;
    public long lastCalculated;

    /**
     * @param currencyName   name of the currency
     * @param amount         amount of the given currency
     * @param lastUsed       last time the currency was used
     * @param accountType    type of account
     * @param accountData    data related to the account
     * @param lastCalculated time the account perks where calculated
     */
    public BankAccount(String currencyName, double amount, long lastUsed, String accountType, String accountData, long lastCalculated) {
        this.currencyName = currencyName;
        this.amount = amount;
        this.lastUsed = lastUsed;
        this.accountType = accountType;
        this.accountData = accountData;
        this.lastCalculated = lastCalculated;
    }
}
