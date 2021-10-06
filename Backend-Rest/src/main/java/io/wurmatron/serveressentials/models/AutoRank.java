package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

import java.util.Objects;

public class AutoRank {

    public String rank;
    public String nextRank;
    public Long playTime;
    public String currencyName;
    public Double currencyAmount;
    public String specialEvents;

    /**
     * @param rank           current rank of the user
     * @param nextRank       rank the user will gain after the requirements
     * @param playtime       amount of time (in minutes)
     * @param currencyName   name of the currency
     * @param currencyAmount amount of the given currency
     * @param specialEvents  special events required to rankup, (Used to extend functionality))
     */
    public AutoRank(String rank, String nextRank, long playtime, String currencyName, double currencyAmount, String specialEvents) {
        this.rank = rank;
        this.nextRank = nextRank;
        this.playTime = playtime;
        this.currencyName = currencyName;
        this.currencyAmount = currencyAmount;
        this.specialEvents = specialEvents;
    }

    public AutoRank() {
    }

    @Override
    public String toString() {
        return ServerEssentialsRest.GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutoRank)) return false;
        AutoRank autoRank = (AutoRank) o;
        return playTime.equals(autoRank.playTime) && Double.compare(autoRank.currencyAmount, currencyAmount) == 0 && Objects.equals(rank, autoRank.rank) && Objects.equals(nextRank, autoRank.nextRank) && Objects.equals(currencyName, autoRank.currencyName) && Objects.equals(specialEvents, autoRank.specialEvents);
    }

    @Override
    public AutoRank clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, AutoRank.class);
    }
}
