package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

import java.util.Objects;

public class Currency {

    public String display_name;
    public Double global_worth;
    public Double sell_worth;
    public Double tax;

    /**
     * @param displayName name used to display this currency
     * @param globalWorth how much of this is worth of the global worth '1'
     * @param sellWorth   how much the can be sold for of the global currency
     * @param tax         how much tax's are taken on transactions of this currency
     */
    public Currency(String displayName, double globalWorth, double sellWorth, double tax) {
        this.display_name = displayName;
        this.global_worth = globalWorth;
        this.sell_worth = sellWorth;
        this.tax = tax;
    }

    public Currency() {
    }

    @Override
    public String toString() {
        return ServerEssentialsRest.GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return Double.compare(currency.global_worth, global_worth) == 0 && Double.compare(currency.sell_worth,
            sell_worth) == 0 && Double.compare(currency.tax, tax) == 0 && Objects.equals(
            display_name, currency.display_name);
    }

    @Override
    public Currency clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, Currency.class);
    }
}
