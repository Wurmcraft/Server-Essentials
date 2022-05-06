/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.routes;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

import io.wurmatron.serveressentials.models.TrackedStat;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class SQLStatistics extends SQLGenerator {

  public static String STATISTICS_TABLE = "statistics";

  /**
   * Creates a new statistic within the DB
   *
   * @param stat instance of the stat to be created
   * @return instance of the statistic that was created
   */
  @Nullable
  public static TrackedStat create(TrackedStat stat) {
    try {
      insert(STATISTICS_TABLE, STATISTICS_COLUMNS, stat, false);
      return stat;
    } catch (Exception e) {
      LOG.debug(
          "Failed to update stat '"
              + stat.event_type
              + "' on '"
              + stat.server_id
              + "' ("
              + e.getMessage()
              + ")");
      LOG.debug("Stat: " + GSON.toJson(stat));
    }
    return null;
  }

  /**
   * Updates the provided stat based on the provided instance
   *
   * @param stat instance of he racked stat to pull the updated information from
   * @param columnsToUpdate columns within the db to be updated
   * @return updated tracked statistic based on the provided information within the db
   */
  public static boolean update(TrackedStat stat, String[] columnsToUpdate) {
    try {
      update(
          STATISTICS_TABLE,
          columnsToUpdate,
          new String[] {"server_id", "uuid", "event_type", "timestamp"},
          new String[] {stat.server_id, stat.uuid, stat.event_type, "" + stat.timestamp},
          stat);
      return true;
    } catch (Exception e) {
      LOG.debug(
          "Failed to update stat ' "
              + stat.server_id
              + "' for '"
              + stat.uuid
              + "' within '"
              + stat.event_type
              + "' ("
              + e.getMessage()
              + ")");
      LOG.debug("Stat: " + GSON.toJson(stat));
    }
    return false;
  }

  /**
   * Get a array / list of all the tracked statistics about the provided user via uuid
   *
   * @param uuid uuid of the user to find the tracked stats for
   * @return a list of all the tracked stats related to the provided uuid
   */
  public static List<TrackedStat> get(String uuid) {
    try {
      return getArray("*", STATISTICS_TABLE, "uuid", uuid, new TrackedStat());
    } catch (Exception e) {
      LOG.debug("Failed to get for uuid '" + uuid + "' (" + e.getMessage() + ")");
    }
    return new ArrayList<>();
  }

  /**
   * Get a array / list of all the tracked statistics for the provided user on the given server
   *
   * @param serverID id of the server to check
   * @param uuid uuid of the user account to lookup
   * @return a list of all the tracked stats related to the proved user on the given serverID
   */
  public static List<TrackedStat> get(String serverID, String uuid) {
    try {
      return getArray(
          "*",
          STATISTICS_TABLE,
          new String[] {"server_id", "uuid"},
          new String[] {serverID, uuid},
          new TrackedStat());
    } catch (Exception e) {
      LOG.debug(
          "Failed to get for uuid '" + uuid + "' on '" + serverID + "' (" + e.getMessage() + ")");
    }
    return new ArrayList<>();
  }

  /**
   * Get a array / list of all the tracked stats related to this event type
   *
   * @param eventType type of event to lookup the statistics
   * @return a list of all the tracked stats related to this event
   */
  public static List<TrackedStat> getType(String eventType) {
    try {
      return getArray("*", STATISTICS_TABLE, "event_type", eventType, new TrackedStat());
    } catch (Exception e) {
      LOG.debug("Failed to get for event '" + eventType + "' (" + e.getMessage() + ")");
    }
    return new ArrayList<>();
  }

  /**
   * Get a array / list of all the tracked stats related to this event type
   *
   * @param eventType type of event to lookup the statistics
   * @param serverID id of the server to find this event on
   * @return a list of all the tracked stats related to this event
   */
  public static List<TrackedStat> getType(String serverID, String eventType) {
    try {
      return getArray(
          "*",
          STATISTICS_TABLE,
          new String[] {"server_id", "event_type"},
          new String[] {serverID, eventType},
          new TrackedStat());
    } catch (Exception e) {
      LOG.debug(
          "Failed to get for event '"
              + eventType
              + "' on '"
              + serverID
              + "' ("
              + e.getMessage()
              + ")");
    }
    return new ArrayList<>();
  }

  /**
   * Delete a statistic about the provided user
   *
   * @param serverID id of the server that's related to this stat
   * @param uuid uuid of the account to remove the tracked stat from
   * @param eventType type of event, to remove the stats from
   * @return if a tracked statistic has been deleted or not.
   */
  public static boolean delete(String serverID, String uuid, String eventType) {
    try {
      delete(
          STATISTICS_TABLE,
          new String[] {"server_id", "uuid", "event_type"},
          new String[] {serverID, uuid, eventType});
      return true;
    } catch (Exception e) {
      LOG.debug(
          "Failed to delete stat for '"
              + uuid
              + "' on '"
              + serverID
              + "' of '"
              + eventType
              + "' ("
              + e.getMessage()
              + ")");
    }
    return false;
  }
}
