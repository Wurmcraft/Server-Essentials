package io.wurmatron.serveressentials.sql;

import io.swagger.models.auth.In;
import joptsimple.internal.Strings;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

/**
 * Direct access to the SQL database / connection Used to interact wit the database
 */
public class SQLGenerator {

  // Columns
  protected static final String[] ACTIONS_COLUMNS = new String[]{"related_id", "host",
      "action", "action_data", "timestamp"};
  protected static final String[] AUTORANKS_COLUMNS = new String[]{"rank", "next_rank",
      "playtime", "currency_name", "currency_amount", "special_events"};
  protected static final String[] BANS_COLUMNS = new String[]{"ban_id", "uuid", "ip",
      "discord_id", "banned_by", "banned_by_type", "ban_reason", "timestamp", "ban_type",
      "ban_data", "ban_status"};
  protected static final String[] CURRENCYS_COLUMNS = new String[]{"currency_id",
      "display_name", "global_worth", "sell_worth", "tax"};
  protected static final String[] DONATOR_COLUMNS = new String[]{"store",
      "transaction_id", "amount", "uuid", "timestamp", "type", "type_data"};
  protected static final String[] LOGGING_COLUMNS = new String[]{"server_id", "timestamp",
      "action_type", "action_data", "uuid", "x", "y", "z", "dim"};
  protected static final String[] MARKETS_COLUMNS = new String[]{"server_id",
      "seller_uuid", "item", "currency_name", "currency_amount", "timestamp",
      "market_type", "market_data", "transfer_id"};
  protected static final String[] RANKS_COLUMNS = new String[]{"rank_id", "name",
      "permissions", "inheritance", "prefix", "prefix_priority", "suffix",
      "suffix_priority", "color", "color_priority"};
  protected static final String[] STATISTICS_COLUMNS = new String[]{"server_id", "uuid",
      "timestamp", "event_type", "event_data"};
  protected static final String[] TRANSFERS_COLUMNS = new String[]{"transfer_id", "uuid",
      "start_time", "items", "server_id"};
  protected static final String[] USERS_COLUMNS = new String[]{"uuid", "username", "rank",
      "perms", "perks", "lang", "muted", "mute_time", "display_name", "discord_id",
      "tracked_time", "wallet", "reward_points", "password_hash", "password_salt",
      "system_perms"};

  protected static DatabaseConnection connection;

  public static DatabaseConnection create() throws Exception {
    return connection = new DatabaseConnection();
  }

  /**
   * Get a certain data instance from the DB
   *
   * @param columns columns that you want to get from the db
   * @param table table that this data is present within
   * @param key key to look for
   * @param data data to look for in the key column
   * @param dataType instance of the data, to be created out of
   * @return instance of the data, with the request data from the db filled in
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws IllegalArgumentException Issue with reflection to add data to the object
   * instance
   */
  protected static <T> T get(String columns, String table, String key, String data,
      T dataType) throws SQLException, IllegalAccessException, IllegalArgumentException {
    String sql = "";
    if (connection.databaseType.equalsIgnoreCase("mysql")) {
      sql = "SELECT " + columns + " FROM `" + table + "` WHERE " + key + "=?;";
    } else if (connection.databaseType.equalsIgnoreCase("postgress")) {
      sql = "SELECT " + columns + " FROM " + table + " WHERE " + key + "=?;";
    }
    PreparedStatement statement = connection.createPrepared(sql);
    if (table.equalsIgnoreCase("bans") || table.equalsIgnoreCase("transfers")) {
      statement.setInt(1, Integer.parseInt(data));
    } else {
      statement.setString(1, data);
    }
    LOG.trace("GET: " + statement);
    return to(statement.executeQuery(), dataType, true);
  }

  /**
   * Get a certain data array from the DB
   *
   * @param columns columns that you want to get from the db
   * @param table table that this data is present within
   * @param key key to look for
   * @param data data to look for in the key column
   * @param dataType instance of the data, to be created out of
   * @return instance of the data, with the request data from the db filled in
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws IllegalArgumentException Issue with reflection to add data to the object
   * instance
   * @throws InstantiationException Issues with reflection, trying to copy requested
   * object instance to fill in data
   */
  protected static <T> List<T> getArray(String columns, String table, String key,
      String data, T dataType)
      throws SQLException, IllegalAccessException, InstantiationException {
    String sql = "";
    if (connection.databaseType.equalsIgnoreCase("mysql")) {
      sql =  "SELECT " + columns + " FROM '" + table + "'";
    } else if (connection.databaseType.equalsIgnoreCase("postgress")) {
      sql =  "SELECT " + columns + " FROM " + table + "";
    }
    if (!key.isEmpty() && !data.isEmpty()) {
      sql = sql + " WHERE " + key + "=?;";
    }
    PreparedStatement statement = connection.createPrepared(sql);
    if (sql.contains("?")) {
      statement.setString(1, data);
    }
    LOG.trace("GET ARR: " + statement);
    return toArray(statement.executeQuery(), dataType);
  }

  /**
   * Get a certain data array from the DB
   *
   * @param columns columns that you want to get from the db
   * @param table table that this data is present within
   * @param key keys to look for
   * @param data data to look for in the key column
   * @param dataType instance of the data, to be created out of
   * @return instance of the data, with the request data from the db filled in
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws IllegalArgumentException Issue with reflection to add data to the object
   * instance
   * @throws InstantiationException Issues with reflection, trying to copy requested
   * object instance to fill in data
   */
  protected static <T> List<T> getArray(String columns, String table, String[] key,
      String[] data, T dataType)
      throws SQLException, IllegalAccessException, InstantiationException {
    StringBuilder sql = new StringBuilder(
        "SELECT " + columns + " FROM " + table + " WHERE ");
    for (int x = 0; x < key.length; x++) {
      sql.append(key[x]).append("=? ").append("AND ");
    }
    String slq = sql.substring(0, sql.length() - 4);
    PreparedStatement statement = connection.createPrepared(slq + ";");
    for (int x = 1; x < key.length + 1; x++) {
      statement.setString(x, data[x - 1]);
    }
    LOG.trace("GET ARR: " + statement);
    return toArray(statement.executeQuery(), dataType);
  }

  protected static <T> List<T> getArray(String columns, String table, String[] key,
      String[] data, T dataType, Class<?>[] types)
      throws SQLException, IllegalAccessException, InstantiationException {
    StringBuilder sql = new StringBuilder(
        "SELECT " + columns + " FROM " + table + " WHERE ");
    for (int x = 0; x < key.length; x++) {
      sql.append(key[x]).append("=? ").append("AND ");
    }
    String slq = sql.substring(0, sql.length() - 4);
    PreparedStatement statement = connection.createPrepared(slq + ";");
    for (int x = 1; x < key.length + 1; x++) {
      if (types[x - 1].equals(String.class)) {
        statement.setString(x, data[x - 1]);
      } else if (types[x - 1].equals(Integer.class)) {
        statement.setInt(x, Integer.parseInt(data[x - 1]));
      }
    }
    LOG.trace("GET ARR: " + statement);
    return toArray(statement.executeQuery(), dataType);
  }

  /**
   * Gets all the data from the DB
   *
   * @param columns columns that you want to get from the db
   * @param table table that this data is present within
   * @param dataType instance of the data, to be created out of
   * @return instance of the data, with the request data from the db filled in
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws InstantiationException Issues with reflection, trying to copy requested
   * object instance to fill in data
   */
  protected static <T> List<T> getAll(String columns, String table, T dataType)
      throws SQLException, IllegalAccessException, InstantiationException {
    PreparedStatement statement = connection.createPrepared(
        "SELECT " + columns + " FROM " + table);
    LOG.trace("GET ALL: " + statement);
    return toArray(statement.executeQuery(), dataType);
  }

  /**
   * Insert new data into the database
   *
   * @param table table to insert this data into
   * @param columns columns you want to insert the data into
   * @param data instance of the data to send to the database
   * @param generatedKey Should return the auto incremented value generated for this sql
   * entry
   * @return see SQL.execute() for more info
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws IllegalArgumentException Issue with reflection to add data to the object
   * instance
   * @throws NoSuchFieldException Issue with collecting the data from the object instance,
   * via reflection
   * @see PreparedStatement#execute()
   */
  protected static <T> int insert(String table, String[] columns, T data,
      boolean generatedKey)
      throws SQLException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
    String sql = "";
    if (connection.databaseType.equalsIgnoreCase("mysql")) {
      sql =
          "INSERT INTO `" + table + "` (`" + String.join("`, `", columns) + "`) VALUES ("
              + argumentGenerator(columns.length, 0, columns) + ")";
    } else if (connection.databaseType.equalsIgnoreCase("postgress")) {
      sql = "INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES ("
          + argumentGenerator(columns.length, 0, columns) + ")";
    }
    PreparedStatement statement = connection.createPrepared(sql,
        generatedKey ? Statement.RETURN_GENERATED_KEYS : 0);
    statement = addArguments(statement, columns, data);
    LOG.info("INSERT: " + statement);
    statement.executeUpdate();
    if (generatedKey) {
      ResultSet set = statement.getGeneratedKeys();
      if (set.next()) {
        return set.getInt(1);
      }
    }
    return -1;
  }

  /**
   * Update's existing data in the database
   *
   * @param table table where the data is being stored
   * @param columnsToUpdate columns to update from the data instance
   * @param key column to look for the data to update
   * @param value value to look for in the key column
   * @param data instance of the data to be updated in the database
   * @return see SQL.execute() for more info
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws IllegalArgumentException Issue with reflection to add data to the object
   * instance
   * @throws NoSuchFieldException Issue with collecting the data from the object instance,
   * via reflection
   * @see PreparedStatement#execute()
   */
  protected static <T> boolean update(String table, String[] columnsToUpdate, String key,
      String value, T data)
      throws SQLException, NoSuchFieldException, IllegalAccessException {
    String sql = "";
    if (connection.databaseType.equalsIgnoreCase("mysql")) {
      sql = "UPDATE `" + table + "` SET " + argumentGenerator(columnsToUpdate.length, 1,
          columnsToUpdate) + " WHERE " + key + "=?;";
    } else if (connection.databaseType.equalsIgnoreCase("postgress")) {
      sql = "UPDATE " + table + " SET " + argumentGenerator(columnsToUpdate.length, 1,
          columnsToUpdate) + " WHERE " + key + "=?;";
    }
    PreparedStatement statement = connection.createPrepared(sql);
    addArguments(statement, columnsToUpdate, data);
    if (table.equalsIgnoreCase("bans") || table.equalsIgnoreCase("transfers")) {
      statement.setInt(columnsToUpdate.length + 1, Integer.parseInt(value));
    } else {
      statement.setString(columnsToUpdate.length + 1, value);
    }
    LOG.trace("UPDATE: " + statement);
    return statement.execute();
  }

  /**
   * Update's existing data in the database
   *
   * @param table table where the data is being stored
   * @param columnsToUpdate columns to update from the data instance
   * @param key column to look for the data to update
   * @param value value to look for in the key column
   * @param data instance of the data to be updated in the database
   * @return see SQL.execute() for more info
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws IllegalArgumentException Issue with reflection to add data to the object
   * instance
   * @throws NoSuchFieldException Issue with collecting the data from the object instance,
   * via reflection
   * @see PreparedStatement#execute()
   */
  protected static <T> boolean update(String table, String[] columnsToUpdate,
      String[] key, String[] value, T data)
      throws SQLException, NoSuchFieldException, IllegalAccessException {
    String sql = "";
    if (connection.databaseType.equalsIgnoreCase("mysql")) {
      sql = "UPDATE `" + table + "` SET " + argumentGenerator(columnsToUpdate.length, 1,
          columnsToUpdate) + " WHERE ";
    } else if (connection.databaseType.equalsIgnoreCase("postgress")) {
      sql = "UPDATE " + table + " SET " + argumentGenerator(columnsToUpdate.length, 1,
          columnsToUpdate) + " WHERE ";
    }
    StringBuilder sqlBuilder = new StringBuilder(sql);
    for (int x = 0; x < key.length; x++) {
      sqlBuilder.append(key[x]).append("=? ").append("AND ");
    }
    String slq = sqlBuilder.substring(0, sqlBuilder.length() - 4);
    PreparedStatement statement = connection.createPrepared(slq + ";");
    addArguments(statement, columnsToUpdate, data);
    for (int x = 1; x < key.length + 1; x++) {
      statement.setString(columnsToUpdate.length + x, value[x - 1]);
    }
    LOG.trace("UPDATE: " + statement);
    statement.execute();
    return true;
  }

  protected static <T> boolean update(String table, String[] columnsToUpdate,
      String[] key, String[] value, T data, Class<?>[] types)
      throws SQLException, NoSuchFieldException, IllegalAccessException {
    String sql = "";
    if (connection.databaseType.equalsIgnoreCase("mysql")) {
      sql = "UPDATE `" + table + "` SET " + argumentGenerator(columnsToUpdate.length, 1,
          columnsToUpdate) + " WHERE ";
    } else if (connection.databaseType.equalsIgnoreCase("postgress")) {
      sql = "UPDATE " + table + " SET " + argumentGenerator(columnsToUpdate.length, 1,
          columnsToUpdate) + " WHERE ";
    }
    StringBuilder sqlBuilder = new StringBuilder(sql);
    for (int x = 0; x < key.length; x++) {
      sqlBuilder.append(key[x]).append("=? ").append("AND ");
    }
    String slq = sqlBuilder.substring(0, sqlBuilder.length() - 4);
    PreparedStatement statement = connection.createPrepared(slq + ";");
    addArguments(statement, columnsToUpdate, data);
    for (int x = 1; x < key.length + 1; x++) {
      if (types[x - 1].equals(String.class)) {
        statement.setString(columnsToUpdate.length + x, value[x - 1]);
      }
      if (types[x - 1].equals(Integer.class)) {
        statement.setInt(columnsToUpdate.length + x, Integer.parseInt(value[x - 1]));
      }
    }
    LOG.trace("UPDATE: " + statement);
    statement.execute();
    return true;
  }

//    UPDATE actions SET action_data = '{data}' WHERE related_id='string' AND host='string' AND action='string' AND timestamp='1651713900'

  /**
   * Delete the given account from the database
   *
   * @param table table where the data is stored
   * @param key column to delete the data instance from
   * @param value column / key value to look for the data object
   * @return see SQL.execute() for more info
   * @throws SQLException A SQL Error has occurred while running the request
   * @see PreparedStatement#execute()
   */
  protected static boolean delete(String table, String key, String value)
      throws SQLException {
    PreparedStatement statement = connection.createPrepared(
        "DELETE FROM " + table + " WHERE " + key + "=?;");
    if (table.equals("bans") || table.equalsIgnoreCase("transfers")) {
      statement.setInt(1, Integer.parseInt(value));
    } else {
      statement.setString(1, value);
    }
    LOG.trace("DELETE: " + statement);
    return statement.execute();
  }

  /**
   * Delete the given account from the database
   *
   * @param table table where the data is stored
   * @param key column to delete the data instance from
   * @param value column / key value to look for the data object
   * @return see SQL.execute() for more info
   * @throws SQLException A SQL Error has occurred while running the request
   * @see PreparedStatement#execute()
   */
  protected static boolean delete(String table, String[] key, String[] value)
      throws SQLException {
    StringBuilder sql = new StringBuilder("DELETE FROM " + table + " WHERE ");
    for (int x = 0; x < key.length; x++) {
      sql.append(key[x]).append("=? ").append("AND ");
    }
    String slq = sql.substring(0, sql.length() - 4);
    PreparedStatement statement = connection.createPrepared(slq + ";");
    for (int x = 1; x < key.length + 1; x++) {
      statement.setString(x, value[x - 1]);
    }
    LOG.trace("DELETE: " + statement);
    return statement.execute();
  }

  /**
   * Reflection casts a ResultSet from the database into a usable data object
   *
   * @param result ResultSet provided from the database.
   * @param dataType data instance to collect the data for
   * @return instance of the provided instance with the data from the database
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws IllegalArgumentException Issue with reflection to add data to the object
   * instance
   */
  protected static <T> T to(ResultSet result, T dataType, boolean next)
      throws SQLException, IllegalAccessException, IllegalArgumentException {
    if (!next || result.next()) {
      for (Field field : dataType.getClass().getDeclaredFields()) {
        try {
          String test = result.getString(field.getName());
          Object obj = result.getObject(field.getName()); // null? how!?!?
          Class<?> fieldType = field.getType();
          boolean isArray = fieldType.isArray();
          boolean str = obj instanceof String;
          if (str && isArray && fieldType.equals(String[].class)) {
            String[] data = ((String) obj).split(",");
            for (int index = 0; index < data.length; index++) {
              data[index] = data[index].trim();
            }
            field.set(dataType, data);
          } else if (str && fieldType.equals(long.class) || str && fieldType.equals(
              Long.class)) {
            field.set(dataType, Long.parseLong((String) obj));
          } else if (str && fieldType.equals(int.class) || str && fieldType.equals(
              Integer.class)) {
            field.set(dataType, Integer.parseInt((String) obj));
          } else if (str && fieldType.equals(float.class) || str && fieldType.equals(
              Float.class)) {
            field.set(dataType, Float.parseFloat((String) obj));
          } else if (str && fieldType.equals(double.class) || str && fieldType.equals(
              Double.class) || str && fieldType.equals(BigDecimal.class)) {
            field.set(dataType, Double.parseDouble((String) obj));
          } else if (str && isJson(fieldType)) {
            field.set(dataType, GSON.fromJson((String) obj, fieldType));
          } else if (obj instanceof BigDecimal && fieldType.equals(Double.class)) {
            field.set(dataType, ((BigDecimal) obj).doubleValue());
          } else if (fieldType.equals(Long.class)) {
            if (obj instanceof Integer) {
              int data = (int) obj;
              field.set(dataType, (long) data);
            } else {
              long data = (long) obj;
              field.set(dataType, (long) data);
            }
          } else if (fieldType.equals(Integer.class)) {
            int data = (int) obj;
            field.set(dataType, data);
          } else if (fieldType.equals(Double.class)) {
            if (obj instanceof Integer) {
              int data = (int) obj;
              field.set(dataType, (double) data);
            } else if (obj instanceof BigDecimal) {
              BigDecimal bigDec = (BigDecimal) obj;
              field.set(dataType, bigDec.doubleValue());
            } else {
              field.set(dataType, obj);
            }
          } else if (isJson(fieldType) && connection.databaseType.equalsIgnoreCase(
              "postgress") && obj instanceof PGobject) {
            field.set(dataType, GSON.fromJson(((PGobject) obj).getValue(), fieldType));
          } else {
            field.set(dataType, obj);
          }
        } catch (Exception e) {
          LOG.warn("Failed to convert! (" + e.getMessage() + ")");
        }
      }
      return dataType;
    } else {
      return null;
    }
  }

  private static boolean isJson(Class<?> fieldType) {
    try {
      fieldType.asSubclass(SQLJson[].class);
      return true;
    } catch (Exception e) {
    }
    try {
      fieldType.asSubclass(SQLJson.class);
      return true;
    } catch (Exception e) {
    }
    return false;
  }

  /**
   * Reflection casts a ResultSet from the database into a usable data object array
   *
   * @param result ResultSet provided from the database.
   * @param dataType data instance to collect the data for
   * @return instance of the provided instance with the data from the database
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws IllegalAccessException Issue with reflection to add data to the object
   * instance
   * @throws IllegalArgumentException Issue with reflection to add data to the object
   * instance
   * @throws InstantiationException Issue with reflection trying to create a new object
   * instance
   */
  protected static <T> List<T> toArray(ResultSet result, T dataType)
      throws SQLException, IllegalAccessException, IllegalArgumentException, InstantiationException {
    List<T> dataArr = new ArrayList<>();
    while (result.next()) {
      // Attempt to create new instance and set values
      T data = (T) dataType.getClass().newInstance();
      T temp = to(result, data, false);
      if (temp != null) {
        dataArr.add(temp);
      }
    }
    return dataArr;
  }

  /**
   * Generates the string for use with SQLGenerator params, based on the amount needed and
   * its columns
   *
   * @param count amount of arguments to generate
   * @param format format of the arguments to generate
   * @param columns list of columns in the table
   * @return a pre-formatted string for use within a sql statement using params
   */
  private static String argumentGenerator(int count, int format, String[] columns) {
    if (count > 0) {
      if (format == 0) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < count; x++) {
          builder.append("?, ");
        }
        return builder.substring(0, builder.length() - 2);
      } else if (format == 1 && columns != null && columns.length > 0) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < count; x++) {
          if (connection.databaseType.equalsIgnoreCase("mysql")) {
            builder.append("`").append(columns[x]).append("`").append(" = ?, ");
          } else {
            builder.append(columns[x]).append(" = ?, ");
          }
        }
        return builder.substring(0, builder.length() - 2);
      }
    }
    return "";
  }

  /**
   * Adds the parameters / arguments into a sql statement, collected from a data instance
   *
   * @param pStatement sql statement to add the arguments onto
   * @param columns columns of the data for the SQL request
   * @param data data to collect the data to be used for the params
   * @throws SQLException A SQL Error has occurred while running the request
   * @throws NoSuchFieldException Issue with reflection to get data from the object
   * instance
   * @throws IllegalAccessException Issue with reflection to get data from the object
   * instance
   */
  private static <T> PreparedStatement addArguments(PreparedStatement pStatement,
      String[] columns, T data)
      throws SQLException, NoSuchFieldException, IllegalAccessException {
    for (int index = 0; index < columns.length; index++) {
      Object fieldData = data.getClass().getDeclaredField(columns[index]).get(data);
      // Ignore null entries
      if (fieldData == null) {
        pStatement.setNull(index + 1, Types.NULL);
        continue;
      }
      // Check for Special Cases
      if (fieldData instanceof String[]) {
        pStatement.setObject(index + 1,
            ((String[]) fieldData).length > 0 ? Strings.join(((String[]) fieldData), ", ")
                : " ");
        continue;
      }
      if (fieldData instanceof SQLJson[] || fieldData instanceof SQLJson) {
        if (connection.databaseType.equalsIgnoreCase("postgress")) {
          PGobject json = new PGobject();
          json.setType("json");
          json.setValue(GSON.toJson(fieldData).replaceAll("\n", "").replaceAll(" ", ""));
          pStatement.setObject(index + 1, json);
          continue;
        }
        pStatement.setObject(index + 1,
            GSON.toJson(fieldData).replaceAll("\n", "").replaceAll(" ", ""));
        continue;
      }
      // Not a special case
      pStatement.setObject(index + 1, fieldData);
    }
    return pStatement;
  }

  /**
   * Mimics how a database update is completed, without the need to request the update
   * from the database
   *
   * @param columnsToUpdate columns in the database that have been updated
   * @param updateData data that was used to update the database
   * @param localInfo data from the database, before the update
   * @return Updated version of the data, (should be in-sync with the database)
   * @throws NoSuchFieldException Issue with reflection to collect data from the object
   * instance
   * @throws IllegalAccessException Issue with reflection to collect data from the object
   * instance
   */
  protected static <T> T updateInfoLocal(String[] columnsToUpdate, T updateData,
      T localInfo) throws NoSuchFieldException, IllegalAccessException {
    for (String column : columnsToUpdate) {
      Field field = updateData.getClass().getDeclaredField(column);
      field.set(localInfo, field.get(updateData));
    }
    return localInfo;
  }
}
