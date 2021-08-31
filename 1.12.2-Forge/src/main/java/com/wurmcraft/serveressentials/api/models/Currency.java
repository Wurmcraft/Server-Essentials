package com.wurmcraft.serveressentials.api.models;

import java.util.Objects;

public class Currency {

    public Long currencyID;
    public String displayName;
    public Double globalWorth;
    public Double sellWorth;
    public Double tax;

    /**
     * @param id          id of the given currency (Don't change as this is used internally to track)
     * @param displayName name used to display this currency
     * @param globalWorth how much of this is worth of the global worth '1'
     * @param sellWorth   how much the can be sold for of the global currency
     * @param tax         how much tax's are taken on transactions of this currency
     */
    public Currency(long id, String displayName, double globalWorth, double sellWorth, double tax) {
        this.currencyID = id;
        this.displayName = displayName;
        this.globalWorth = globalWorth;
        this.sellWorth = sellWorth;
        this.tax = tax;
    }

    public Currency() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return currencyID.equals(currency.currencyID)
                && Double.compare(currency.globalWorth, globalWorth) == 0
                && Double.compare(currency.sellWorth, sellWorth) == 0
                && Double.compare(currency.tax, tax) == 0
                && Objects.equals(displayName, currency.displayName);
    }
}
