package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.account.BankAccount;
import io.wurmatron.serveressentials.models.account.ServerTime;

public class Account {

    public String uuid;
    public String username;
    public String[] rank;
    public String[] perms;
    public String[] perks;
    public String language;
    public Boolean muted;
    public Long muteTime;
    public String displayName;
    public String discordID;
    public ServerTime[] trackedTime;
    public BankAccount[] wallet;
    public Integer rewardPoints;
    public String passwordHash;
    public String passwordSalt;
    public String[] systemPerms;

    /**
     * @param uuid         uuid of the given user
     * @param username     username of the given user
     * @param rank         ranks of the given user
     * @param perms        extra perms of the given user
     * @param perks        extra perms of the given user
     * @param language     language of the given user
     * @param muted        is the user muted
     * @param muteTime     How long until the user will be unmute'd
     * @param displayName  user's displayName, "Nickname"
     * @param discordID    user's discordID
     * @param trackedTime  list of instances of the user's playtime
     * @param wallet       list of bank accounts
     * @param rewardPoints points the user has earned
     * @param passwordHash hash of the user's system password
     * @param passwordSalt salt of the user's password
     * @param systemPerms  permissions to interact with the system
     */
    public Account(String uuid, String username, String[] rank, String[] perms, String[] perks, String language, boolean muted, long muteTime, String displayName, String discordID, ServerTime[] trackedTime, BankAccount[] wallet, int rewardPoints, String passwordHash, String passwordSalt, String[] systemPerms) {
        this.uuid = uuid;
        this.username = username;
        this.rank = rank;
        this.perms = perms;
        this.perks = perks;
        this.language = language;
        this.muted = muted;
        this.muteTime = muteTime;
        this.displayName = displayName;
        this.discordID = discordID;
        this.trackedTime = trackedTime;
        this.wallet = wallet;
        this.rewardPoints = rewardPoints;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.systemPerms = systemPerms;
    }

    public Account() {
    }

    @Override
    public Account clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, Account.class);
    }
}
