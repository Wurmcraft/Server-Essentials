package com.wurmcraft.serveressentials.api.models;

import java.util.Objects;

public class AutoRank {

    public Integer autoRankID;
    public String rank;
    public String nextRank;
    public Long playTime;
    public String currencyName;
    public Double currencyAmount;
    public String specialEvents;

    /**
     * @param autoRankID     id of the given rank (Don't change as this is used internally to track)
     * @param rank           current rank of the user
     * @param nextRank       rank the user will gain after the requirements
     * @param playtime       amount of time (in minutes)
     * @param currencyName   name of the currency
     * @param currencyAmount amount of the given currency
     * @param specialEvents  special events required to rankup, (Used to extend functionality))
     */
    public AutoRank(
            Integer autoRankID,
            String rank,
            String nextRank,
            long playtime,
            String currencyName,
            double currencyAmount,
            String specialEvents) {
        this.autoRankID = autoRankID;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutoRank)) return false;
        AutoRank autoRank = (AutoRank) o;
        return autoRankID.equals(autoRank.autoRankID)
                && playTime.equals(autoRank.playTime)
                && Double.compare(autoRank.currencyAmount, currencyAmount) == 0
                && Objects.equals(rank, autoRank.rank)
                && Objects.equals(nextRank, autoRank.nextRank)
                && Objects.equals(currencyName, autoRank.currencyName)
                && Objects.equals(specialEvents, autoRank.specialEvents);
    }
}
