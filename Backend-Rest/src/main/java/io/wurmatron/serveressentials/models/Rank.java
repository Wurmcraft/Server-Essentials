package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

import java.util.Arrays;
import java.util.Objects;

public class Rank {

    public Long rankID;
    public String name;
    public String[] permissions;
    public String[] inheritance;
    public String prefix;
    public Integer prefixPriority;
    public String suffix;
    public Integer suffixPriority;
    public String color;
    public Integer colorPriority;

    /**
     * @param rankID         id of the given rank (Don't change as this is used internally to track)
     * @param name           name of the given rank
     * @param permissions    permissions this rank has been given
     * @param inheritance    inheritance, "rank ladder"
     * @param prefix         prefix for this rank
     * @param prefixPriority priority of this rank's prefix, default 0
     * @param suffix         suffix for this rank
     * @param suffixPriority suffix for this rank's suffix, default 0
     * @param color          color to used for displaying this rank
     * @param colorPriority  priority of this color, default 0
     */
    public Rank(long rankID, String name, String[] permissions, String[] inheritance, String prefix, int prefixPriority, String suffix, int suffixPriority, String color, int colorPriority) {
        this.rankID = rankID;
        this.name = name;
        this.permissions = permissions;
        this.inheritance = inheritance;
        this.prefix = prefix;
        this.prefixPriority = prefixPriority;
        this.suffix = suffix;
        this.suffixPriority = suffixPriority;
        this.color = color;
        this.colorPriority = colorPriority;
    }

    public Rank() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rank)) return false;
        Rank rank = (Rank) o;
        return rankID.equals(rank.rankID) && prefixPriority.equals(rank.prefixPriority) && suffixPriority.equals(rank.suffixPriority) && colorPriority.equals(rank.colorPriority) && name.equals(rank.name) && Arrays.equals(permissions, rank.permissions) && Arrays.equals(inheritance, rank.inheritance) && Objects.equals(prefix, rank.prefix) && Objects.equals(suffix, rank.suffix) && Objects.equals(color, rank.color);
    }

    @Override
    public Rank clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, Rank.class);
    }
}
