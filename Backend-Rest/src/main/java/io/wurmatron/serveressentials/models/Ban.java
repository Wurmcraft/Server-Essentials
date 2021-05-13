package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

import java.util.Objects;

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
     * @param banID        id of the given ban (Don't change as this is used internally to track)
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

    public Ban() {
    }

    @Override
    public String toString() {
        return ServerEssentialsRest.GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ban)) return false;
        Ban ban = (Ban) o;
        return banID == ban.banID && timestamp == ban.timestamp && Objects.equals(uuid, ban.uuid) && Objects.equals(discordID, ban.discordID) && Objects.equals(bannedBy, ban.bannedBy) && Objects.equals(bannedByType, ban.bannedByType) && Objects.equals(banReason, ban.banReason) && Objects.equals(banType, ban.banType) && Objects.equals(banData, ban.banData);
    }

    @Override
    public Rank clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, Rank.class);
    }
}
