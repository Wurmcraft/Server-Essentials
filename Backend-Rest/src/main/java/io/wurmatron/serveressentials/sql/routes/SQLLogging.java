package io.wurmatron.serveressentials.sql.routes;

import io.swagger.models.auth.In;
import io.wurmatron.serveressentials.models.LogEntry;
import io.wurmatron.serveressentials.sql.SQLGenerator;

import javax.annotation.Nullable;
import java.util.*;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class SQLLogging extends SQLGenerator {

  public static String LOGGING_TABLE = "logging";

  /**
   * Creates a new log entry
   *
   * @param entry instance of the log entry to create
   * @return instance of the log entry that was created
   * @see SQLGenerator#insert(String, String[], Object, boolean)
   */
  @Nullable
  public static LogEntry create(LogEntry entry) {
    try {
      insert(LOGGING_TABLE, LOGGING_COLUMNS, entry, false);
      return entry;
    } catch (Exception e) {
      LOG.debug("Failed to create log entry for '" + entry.action_type + "' at '"
          + entry.timestamp + "' (" + e.getMessage() + ")");
      LOG.debug("Log Entry: " + GSON.toJson(entry));
    }
    return null;
  }

  /**
   * Updates the given log entry in the db
   *
   * @param entry Log Entry to create the update from
   * @param columnsToUpdate columns in the db to update
   * @return if the update was completed without errors
   */
  public static boolean update(LogEntry entry, String[] columnsToUpdate) {
    try {
      update(LOGGING_TABLE, columnsToUpdate,
          new String[]{"action_type", "x", "y", "z", "dim", "timestamp", "uuid"},
          new String[]{entry.action_type, "" + entry.x, "" + entry.y, "" + entry.z,
              "" + entry.dim, "" + entry.timestamp, entry.uuid}, entry, new Class[] {String.class, Integer.class, Integer.class, Integer.class,Integer.class,String.class, String.class});
      return true;
    } catch (Exception e) {
      LOG.debug("Failed to update log entry '" + entry.action_type + "' " + entry.x + ", "
          + entry.y + ", " + entry.z + ", " + entry.dim + "' (" + e.getMessage() + ")");
      LOG.debug("Log Entry: " + GSON.toJson(entry));
    }
    return false;
  }

  /**
   * Get a array / list of entries for the requested location
   *
   * @param serverID id of the server that this log entry was created on
   * @param x x position where this entry took place
   * @param y y position where this entry took place
   * @param z z position where this entry took place
   * @param dim dimension where this entry took place
   * @return a list of entries that took place at this location
   */
  public static List<LogEntry> get(String serverID, int x, int y, int z, int dim) {
    try {
      return getArray("*", LOGGING_TABLE, new String[]{"server_id", "x", "y", "z", "dim"},
          new String[]{serverID, "" + x, "" + y, "" + z, "" + dim}, new LogEntry(),
          new Class[]{String.class,
              Integer.class, Integer.class, Integer.class, Integer.class});
    } catch (Exception e) {
      LOG.debug(
          "Failed to get log entry for '" + serverID + " '" + x + ", " + y + ", " + z
              + ", " + dim);
    }
    return null;
  }

  /**
   * Get a array / list of the entries for the requested user with the specified
   * action-type
   *
   * @param serverID if of the server that this log entry was created on
   * @param actionType event / action that took place
   * @param uuid uuid or the account that created the requested log entry
   * @return a list of entries that are related to the provided user with the requested
   * type
   */
  public static List<LogEntry> get(String serverID, String actionType, String uuid) {
    try {
      return getArray("*", LOGGING_TABLE,
          new String[]{"server_id", "action_type", "uuid"},
          new String[]{serverID, actionType, uuid}, new LogEntry());
    } catch (Exception e) {
      LOG.debug("Failed to get log entry for '" + serverID + " with type '" + actionType
          + "' for '" + uuid + "' (" + e.getMessage() + ")");
    }
    return null;
  }

  /**
   * Deletes the requested log entry from the db
   *
   * @param serverID if of the server that this log entry was created on
   * @param actionType event / action that took place
   * @param uuid uuid of the account / user that created this log-entry
   * @param timestamp unix timestamp when this log-entry was created
   * @return if the delete was successfully without any errors
   */
  public static boolean delete(String serverID, String actionType, String uuid,
      long timestamp) {
    try {
      delete(LOGGING_TABLE, new String[]{"server_id", "action_type", "uuid", "timestamp"},
          new String[]{serverID, actionType, uuid, "" + timestamp});
      return true;
    } catch (Exception e) {
      LOG.debug("Failed to delete log entry at '" + timestamp + "' on '" + serverID
          + "' for type '" + actionType + "'");
    }
    return false;
  }
}
