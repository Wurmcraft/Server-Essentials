package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

public class Currency {

    public long id;
    public String displayName;
    public double globalWorth;
    public double sellWorth;
    public double tax;

    /**
     * @param id          id of the given currency (Don't change as this is used internally to track)
     * @param displayName name used to display this currency
     * @param globalWorth how much of this is worth of the global worth '1'
     * @param sellWorth   how much the can be sold for of the global currency
     * @param tax         how much tax's are taken on transactions of this currency
     */
    public Currency(long id, String displayName, double globalWorth, double sellWorth, double tax) {
        this.id = id;
        this.displayName = displayName;
        this.globalWorth = globalWorth;
        this.sellWorth = sellWorth;
        this.tax = tax;
    }

    @Override
    public String toString() {
        return ServerEssentialsRest.GSON.toJson(this);
    }
}
