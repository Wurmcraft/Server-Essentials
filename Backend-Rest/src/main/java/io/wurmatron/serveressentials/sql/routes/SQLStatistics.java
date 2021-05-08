package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.TrackedStat;
import io.wurmatron.serveressentials.sql.SQLGenerator;

import javax.annotation.Nullable;
import java.util.*;

public class SQLStatistics extends SQLGenerator {

    public static String STATISTICS_TABLE = "statistics";

    /**
     * Creates a new statistic within the DB
     *
     * @param stat instance of the stat to be created
     * @return instance of the statistic that was created
     */
    // TODO Implement
    @Nullable
    public static TrackedStat create(TrackedStat stat) {
        return null;
    }

    /**
     * Updates the provided stat based on the provided instance
     *
     * @param stat            instance of he racked stat to pull the updated information from
     * @param columnsToUpdate columns within the db to be updated
     * @return updated tracked statistic based on the provided information within the db
     */
    // TODO Implement
    @Nullable
    public static boolean update(TrackedStat stat, String[] columnsToUpdate) {
        return false;
    }

    /**
     * Get a array / list of all the tracked statistics about the provided user via uuid
     *
     * @param uuid uuid of the user to find the tracked stats for
     * @return a list of all the tracked stats related to the provided uuid
     */
    // TODO Implement
    public static List<TrackedStat> get(String uuid) {
        return null;
    }

    /**
     * Get a array / list of all the tracked statistics for the provided user on the given server
     *
     * @param serverID id of the server to check
     * @param uuid     uuid of the user account to lookup
     * @return a list of all the tracked stats related to the proved user on the given serverID
     */
    // TODO Implement
    public static List<TrackedStat> get(String serverID, String uuid) {
        return null;
    }

    /**
     * Get a array / list of all the tracked stats related to this event type
     *
     * @param eventType type of event to lookup the statistics
     * @return a list of all the tracked stats related to this event
     */
    // TODO Implement
    public static List<TrackedStat> getType(String eventType) {
        return null;
    }

    /**
     * Get a array / list of all the tracked stats related to this event type
     *
     * @param eventType type of event to lookup the statistics
     * @param serverID  id of the server to find this event on
     * @return a list of all the tracked stats related to this event
     */
    // TODO Implement
    public static List<TrackedStat> getType(String serverID, String eventType) {
        return null;
    }

    /**
     * Delete a statistic about the provided user
     *
     * @param serverID  id of the server that's related to this stat
     * @param uuid      uuid of the account to remove the tracked stat from
     * @param eventType type of event, to remove the stats from
     * @return if a tracked statistic has been deleted or not.
     */
    // TODO Implement
    public static boolean delete(String serverID, String uuid, String eventType) {
        return false;
    }
}
