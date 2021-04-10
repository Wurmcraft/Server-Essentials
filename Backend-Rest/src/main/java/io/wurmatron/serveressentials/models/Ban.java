package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

public class Ban {

    public long banID;
    public String uuid;
    public String discordID;
    public String bannedBy;
    public String bannedByType;
    public String banReason;
    public long timestamp;
    public String banType;
    public String banData;

    /**
     * @param banID           id of the given ban (Don't change as this is used internally to track)
     * @param uuid         uuid of the given user
     * @param discordID    discord ID of the given user
     * @param bannedBy     uuid or discordID of the person that banned the specified user
     * @param bannedByType "discord" or "minecraft"
     * @param banReason    reason the person was banned
     * @param timestamp    Unix Timestamp for when the action occurred
     * @param banType      type of ban, "TEMP", "PERMANENT", "SPECIAL"
     * @param banData      extra data related to the ban
     */
    public Ban(long banID, String uuid, String discordID, String bannedBy, String bannedByType, String banReason, long timestamp, String banType, String banData) {
        this.banID = banID;
        this.uuid = uuid;
        this.discordID = discordID;
        this.bannedBy = bannedBy;
        this.bannedByType = bannedByType;
        this.banReason = banReason;
        this.timestamp = timestamp;
        this.banType = banType;
        this.banData = banData;
    }


    @Override
    public String toString() {
        return ServerEssentialsRest.GSON.toJson(this);
    }
}
