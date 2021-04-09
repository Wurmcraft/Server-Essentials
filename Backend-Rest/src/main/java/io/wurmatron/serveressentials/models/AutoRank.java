package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

public class AutoRank {

    public long id;
    public String rank;
    public String nextRank;
    public long playtime;
    public String currencyName;
    public double currencyAmount;
    public String specialEvents;

    /**
     * @param id             id of the given rank (Don't change as this is used internally to track)
     * @param rank           current rank of the user
     * @param nextRank       rank the user will gain after the requirements
     * @param playtime       amount of time (in minutes)
     * @param currencyName   name of the currency
     * @param currencyAmount amount of the given currency
     * @param specialEvents  special events required to rankup, (Used to extend functionality))
     */
    public AutoRank(long id, String rank, String nextRank, long playtime, String currencyName, double currencyAmount, String specialEvents) {
        this.id = id;
        this.rank = rank;
        this.nextRank = nextRank;
        this.playtime = playtime;
        this.currencyName = currencyName;
        this.currencyAmount = currencyAmount;
        this.specialEvents = specialEvents;
    }

    @Override
    public String toString() {
        return ServerEssentialsRest.GSON.toJson(this);
    }
}
