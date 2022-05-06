package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

import java.util.Objects;

public class Ban {

    public long ban_id;
    public String uuid;
    public String ip;
    public String discord_id;
    public String banned_by;
    public String banned_by_type;
    public String ban_reason;
    public String timestamp;
    public String ban_type;
    public String ban_data;
    public boolean ban_status;

    /**
     * @param banID        id of the given ban (Don't change as this is used internally to track)
     * @param uuid         uuid of the given user
     * @param ip           IP address of the user when banned
     * @param discordID    discord ID of the given user
     * @param bannedBy     uuid or discordID of the person that banned the specified user
     * @param bannedByType "discord" or "minecraft"
     * @param banReason    reason the person was banned
     * @param timestamp    Unix Timestamp for when the action occurred
     * @param banType      type of ban, "TEMP", "PERMANENT", "SPECIAL"
     * @param banData      extra data related to the ban
     * @param banStatus    is this ban still active
     */
    public Ban(long banID, String uuid, String ip, String discordID, String banned_by, String bannedByType, String banReason, String timestamp, String banType, String banData, boolean banStatus) {
        this.ban_id = banID;
        this.uuid = uuid;
        this.ip = ip;
        this.discord_id = discordID;
        this.banned_by = banned_by;
        this.banned_by_type = bannedByType;
        this.ban_reason = banReason;
        this.timestamp = timestamp;
        this.ban_type = banType;
        this.ban_data = banData;
        this.ban_status = banStatus;
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
        return ban_id == ban.ban_id && timestamp == ban.timestamp && ban_status == ban.ban_status && Objects.equals(uuid, ban.uuid) && Objects.equals(ip, ban.ip) && Objects.equals(discord_id, ban.discord_id) && Objects.equals(banned_by, ban.banned_by) && Objects.equals(banned_by_type, ban.banned_by_type) && Objects.equals(ban_reason, ban.ban_reason) && Objects.equals(ban_type, ban.ban_type) && Objects.equals(ban_data, ban.ban_data);
    }

    @Override
    public Ban clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, Ban.class);
    }
}
