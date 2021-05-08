package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.LogEntry;
import io.wurmatron.serveressentials.sql.SQLGenerator;

import javax.annotation.Nullable;
import java.util.*;

public class SQLLogging extends SQLGenerator {

    public static String LOGGING_TABLE = "logging";

    /**
     * Creates a new log entry
     *
     * @param entry instance of the log entry to create
     * @return instance of the log entry that was created
     * @see SQLGenerator#insert(String, String[], Object, boolean)
     */
    // TODO Implement
    @Nullable
    public static LogEntry create(LogEntry entry) {
        return null;
    }

    /**
     * Updates the given log entry in the db
     *
     * @param entry           Log Entry to create the update from
     * @param columnsToUpdate columns in the db to update
     * @return if the update was completed without errors
     */
    // TODO Implement
    public static boolean update(LogEntry entry, String[] columnsToUpdate) {
        return false;
    }

    /**
     * Get a array / list of entries for the requested location
     *
     * @param serverID id of the server that this log entry was created on
     * @param x        x position where this entry took place
     * @param y        y position where this entry took place
     * @param z        z position where this entry took place
     * @param dim      dimension where this entry took place
     * @return a list of entries that took place at this location
     */
    // TODO Implement
    public static List<LogEntry> get(String serverID, int x, int y, int z, int dim) {
        return null;
    }

    /**
     * Get a array / list of the entries for the requested user with the specified action-type
     *
     * @param serverID   if of the server that this log entry was created on
     * @param actionType event / action that took place
     * @param uuid       uuid or the account that created the requested log entry
     * @return a list of entries that are related to the provided user with the requested type
     */
    // TODO Implement
    public static List<LogEntry> get(String serverID, String actionType, String uuid) {
        return null;
    }

    /**
     * Deletes the requested log entry from the db
     *
     * @param serverID   if of the server that this log entry was created on
     * @param actionType event / action that took place
     * @param uuid       uuid of the account / user that created this log-entry
     * @param timestamp  unix timestamp when this log-entry was created
     * @return if the delete was successfully without any errors
     */
    // TODO Implement
    public static boolean delete(String serverID, String actionType, String uuid, long timestamp) {
        return false;
    }
}
