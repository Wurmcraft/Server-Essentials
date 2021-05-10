package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.cache_holder.CacheRank;
import io.wurmatron.serveressentials.sql.cache_holder.CacheRankName;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.*;

public class SQLCacheRank extends SQLCache {

    public static final String RANKS_TABLE = "ranks";

    private static final NonBlockingHashMap<String, CacheRankName> rankNameCache = new NonBlockingHashMap<>();
    private static long lastFullSync = System.currentTimeMillis();

    /**
     * Get a rank based on its id
     *
     * @param id id for the given rank
     * @return instance of the rank
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#get(String, String, String, String, Object)
     * @see #invalidate(long)
     */
    @Nullable
    public static Rank getID(long id) {
        // Attempt to get from cache
        if (rankCache.contains(id))
            if (!needsUpdate(rankCache.get(id)))
                return rankCache.get(id).rank;
            else
                rankCache.remove(id);
        // Not in cache / invalid
        try {
            Rank rank = get("*", RANKS_TABLE, "rankID", "" + id, new Rank());
            if (rank != null) {
                rankCache.put(id, new CacheRank(rank));
                rankNameCache.put(rank.name.toUpperCase(), new CacheRankName(rank.rankID));
                return rank;
            }
        } catch (Exception e) {
            LOG.debug("Failed to find rank with rankID '" + id + "'(" + e.getMessage() + ")");
        }
        // Rank does not exist
        return null;
    }

    /**
     * Get a rank based on its name
     *
     * @param name name of the given rank
     * @return instance of the rank
     */
    @Nullable
    public static Rank get(String name) {
        // Attempt to get from cache
        if (rankNameCache.contains(name.toUpperCase()))
            if (!needsUpdate(rankNameCache.get(name.toUpperCase())))
                return getID(rankNameCache.get(name.toUpperCase()).rankID);
            else
                rankNameCache.remove(name.toUpperCase());
        // Not in cache / invalid
        try {
            Rank rank = get("*", RANKS_TABLE, "name", name.toLowerCase(), new Rank());
            if (rank != null) {
                rankCache.put(rank.rankID, new CacheRank(rank));
                rankNameCache.put(rank.name, new CacheRankName(rank.rankID));
                return rank;
            }
        } catch (Exception e) {
            LOG.debug("Failed to find rank with name '" + name + "'(" + e.getMessage() + ")");
        }
        // Rank (name), does not exist
        return null;
    }

    /**
     * Gets a list of all the ranks
     *
     * @return a list of all the ranks
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#getArray(String, String, String, String, Object)
     */
    public static List<Rank> get() {
        // Attempt to get from cache
        if (lastFullSync + (config.server.cacheTime * 1000L) > System.currentTimeMillis()) {
            List<Rank> ranks = new ArrayList<>();
            for (CacheRank cache : rankCache.values())
                if (!needsUpdate(cache))
                    ranks.add(cache.rank);
                else {  // Update entry
                    invalidate(cache.rank.rankID);
                    Rank rank = getID(cache.rank.rankID);
                    if (rank != null)
                        ranks.add(rank);
                }
        }
        // Full Sync
        try {
            List<Rank> ranks = getArray("*", RANKS_TABLE, "", "", new Rank());
            if (ranks.size() > 0) {
                // Add to cache
                for (Rank rank : ranks) {
                    rankCache.put(rank.rankID, new CacheRank(rank));
                    rankNameCache.put(rank.name.toUpperCase(), new CacheRankName(rank.rankID));
                }
                lastFullSync = System.currentTimeMillis();
                return ranks;
            }
        } catch (Exception e) {
            LOG.debug("Failed to get ranks (" + e.getMessage() + ")");
        }
        return new ArrayList<>();
    }

    /**
     * Creates a new rank
     *
     * @param rank instance to be created
     * @return instance that was created, with id
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#insert(String, String[], Object, boolean)
     */
    public static Rank create(Rank rank) {
        try {
            rank.name = rank.name.toLowerCase();
            rank.rankID = insert(RANKS_TABLE, Arrays.copyOfRange(RANKS_COLUMNS, 1, RANKS_COLUMNS.length), rank, true);
            rankCache.put(rank.rankID, new CacheRank(rank));
            rankNameCache.put(rank.name.toUpperCase(), new CacheRankName(rank.rankID));
            return rank;
        } catch (Exception e) {
            LOG.debug("Failed to add rank with name '" + rank.name + "'(" + e.getMessage() + ")");
            LOG.debug("Rank: " + GSON.toJson(rank));
        }
        return rank;
    }

    /**
     * Updates a existing rank
     *
     * @param rank updated rank instance
     * @return if the rank has been updated or not
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#update(String, String[], String, String, Object)
     */
    public static boolean update(Rank rank, String[] columnsToUpdate) {
        try {
            update(RANKS_TABLE, columnsToUpdate, "rankID", rank.rankID + "", rank);
            if (rankCache.contains(rank.rankID)) {
                rankCache.get(rank.rankID).rank = updateInfoLocal(columnsToUpdate, rank, rankCache.get(rank.rankID).rank);
                rankCache.get(rank.rankID).lastSync = System.currentTimeMillis();
            } else {   // Missing from cache
                rankCache.put(rank.rankID, new CacheRank(rank));
                rankNameCache.put(rank.name.toUpperCase(), new CacheRankName(rank.rankID));
            }
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to update rank with id '" + rank.rankID + "'(" + e.getMessage() + ")");
            LOG.debug("Rank: " + GSON.toJson(rank));
        }
        return false;
    }

    /**
     * Deletes the given rank
     *
     * @param rankID ID of the rank to be deleted
     * @return if the rank has been deleted or not
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#delete(String, String, String)
     * @see #invalidate(long)
     */
    public static boolean delete(long rankID) {
        try {
            delete(RANKS_TABLE, "rankID", rankID + "");
            invalidate(rankID);
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to delete rank with id '" + rankID + "' (" + e.getMessage() + ")");
        }
        return false;
    }

    /**
     * @param rankID Id of the rank to remove from the cache
     */
    public static void invalidate(long rankID) {
        rankCache.remove(rankID);
        LOG.debug("Rank '" + rankID + " has been invalidated, will be updated on next request!");
    }

    /**
     * Cleanup the stored cache and look for expired entries
     */
    public static void cleanupCache() {
        LOG.info("Rank Cache cleanup has begun!");
        // ID Cache
        List<Long> toBeRemoved = new ArrayList<>();
        for (CacheRank rank : rankCache.values())
            if (needsUpdate(rank))
                toBeRemoved.add(rank.rank.rankID);
        // Name Cache
        List<String> toBeRemovedName = new ArrayList<>();
        for (String name : rankNameCache.keySet())
            if (needsUpdate(rankNameCache.get(name)))
                toBeRemovedName.add(name);
        // Remove from Cache
        int count = 0;
        for (Long rankID : toBeRemoved) {
            count++;
            invalidate(rankID);
        }
        for (String name : toBeRemovedName) {
            count++;
            rankNameCache.remove(name);
        }
        LOG.info("RankCache has been cleaned, " + count + " entries have been removed!");
    }

    /**
     * Removes the expired entries from the database
     */
    public static void cleanupDB() {
    }
}
