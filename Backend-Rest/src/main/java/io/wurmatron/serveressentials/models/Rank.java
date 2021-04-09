package io.wurmatron.serveressentials.models;

public class Rank {

    public long rankID;
    public String name;
    public String[] permissions;
    public String[] inheritance;
    public String prefix;
    public int prefixPriority;
    public String suffix;
    public int suffixPriority;
    public String color;
    public int colorPriority;

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
}
