package io.wurmatron.serveressentials.sql;

import joptsimple.internal.Strings;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

/**
 * Direct access to the SQL database / connection
 * Used to interact wit the database
 */
public class SQLGenerator {

    // Columns
    protected static final String[] ACTIONS_COLUMNS = new String[]{"relatedID", "host", "action", "actionData", "timestamp"};
    protected static final String[] AUTORANKS_COLUMNS = new String[]{"autoRankID", "rank", "nextRank", "playTime", "currencyName", "currencyAmount", "specialEvents"};
    protected static final String[] BANS_COLUMNS = new String[]{"banID", "uuid", "discordID", "bannedBy", "banReason", "timestamp", "banType", "banData"};
    protected static final String[] CURRENCYS_COLUMNS = new String[]{"currencyID", "displayName", "globalWorth", "sellWorth", "tax"};
    protected static final String[] DONATOR_COLUMNS = new String[]{"store", "transactionID", "amount", "uuid", "timestamp", "type", "typeData"};
    protected static final String[] LOGGING_COLUMNS = new String[]{"serverID", "timestamp", "actionType", "actionData", "uuid", "x", "y", "z", "dim"};
    protected static final String[] MARKETS_COLUMNS = new String[]{"serverID", "sellerUUID", "item", "currencyName", "currencyAmount", "timestamp", "marketType", "marketData", "transferID"};
    protected static final String[] RANKS_COLUMNS = new String[]{"rankID", "name", "permissions", "inheritance", "prefix", "prefixPriority", "suffix", "suffixPriority", "color", "colorPriority"};
    protected static final String[] STATISTICS_COLUMNS = new String[]{"serverID", "uuid", "timestamp", "eventType", "eventData"};
    protected static final String[] TRANSFERS_COLUMNS = new String[]{"transferID", "uuid", "startTime", "items", "serverID"};
    protected static final String[] USERS_COLUMNS = new String[]{"uuid", "username", "rank", "perms", "perks", "language", "muted", "muteTime", "displayName", "discordID", "trackedTime", "wallet", "rewardPoints", "passwordHash", "passwordSalt", "systemPerms"};

    protected static DatabaseConnection connection;

    public static DatabaseConnection create() throws SQLException {
        return connection = new DatabaseConnection();
    }

    /**
     * Get a certain data instance from the DB
     *
     * @param columns  columns that you want to get from the db
     * @param table    table that this data is present within
     * @param key      key to look for
     * @param data     data to look for in the key column
     * @param dataType instance of the data, to be created out of
     * @return instance of the data, with the request data from the db filled in
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     */
    protected static <T> T get(String columns, String table, String key, String data, T dataType) throws SQLException, IllegalAccessException, IllegalArgumentException {
        PreparedStatement statement = connection.createPrepared("SELECT " + columns + " FROM `" + table + "` WHERE " + key + "=?;");
        statement.setString(1, data);
        LOG.trace("GET: " + statement);
        return to(statement.executeQuery(), dataType, true);
    }

    /**
     * Get a certain data array from the DB
     *
     * @param columns  columns that you want to get from the db
     * @param table    table that this data is present within
     * @param key      key to look for
     * @param data     data to look for in the key column
     * @param dataType instance of the data, to be created out of
     * @return instance of the data, with the request data from the db filled in
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     * @throws InstantiationException   Issues with reflection, trying to copy requested object instance to fill in data
     */
    protected static <T> List<T> getArray(String columns, String table, String key, String data, T dataType) throws SQLException, IllegalAccessException, InstantiationException {
        String sql = "SELECT " + columns + " FROM `" + table + "`";
        if (!key.isEmpty() && !data.isEmpty())
            sql = sql + " WHERE " + key + "=?;";
        PreparedStatement statement = connection.createPrepared(sql);
        if (sql.contains("?"))
            statement.setString(1, data);
        LOG.trace("GET ARR: " + statement);
        return toArray(statement.executeQuery(), dataType);
    }

    /**
     * Gets all the data from the DB
     *
     * @param columns  columns that you want to get from the db
     * @param table    table that this data is present within
     * @param dataType instance of the data, to be created out of
     * @return instance of the data, with the request data from the db filled in
     * @throws SQLException           A SQL Error has occurred while running the request
     * @throws IllegalAccessException Issue with reflection to add data to the object instance
     * @throws InstantiationException Issues with reflection, trying to copy requested object instance to fill in data
     */
    protected static <T> List<T> getAll(String columns, String table, T dataType) throws SQLException, IllegalAccessException, InstantiationException {
        PreparedStatement statement = connection.createPrepared("SELECT " + columns + " FROM `" + table);
        LOG.trace("GET ALL: " + statement);
        return toArray(statement.executeQuery(), dataType);
    }

    /**
     * Insert new data into the database
     *
     * @param table        table to insert this data into
     * @param columns      columns you want to insert the data into
     * @param data         instance of the data to send to the database
     * @param generatedKey Should return the auto incremented value generated for this sql entry
     * @return see SQL.execute() for more info
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     * @throws NoSuchFieldException     Issue with collecting the data from the object instance, via reflection
     * @see PreparedStatement#execute()
     */
    protected static <T> int insert(String table, String[] columns, T data, boolean generatedKey) throws SQLException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        PreparedStatement statement = connection.createPrepared("INSERT INTO `" + table + "` (`" + String.join("`, `", columns) + "`) VALUES (" + argumentGenerator(columns.length, 0, columns) + ")", generatedKey ? Statement.RETURN_GENERATED_KEYS : 0);
        addArguments(statement, columns, data);
        LOG.info("INSERT: " + statement);
        statement.executeUpdate();
        if (generatedKey) {
            ResultSet set = statement.getGeneratedKeys();
            if (set.next())
                return set.getInt(1);
        }
        return -1;
    }

    /**
     * Update's existing data in the database
     *
     * @param table           table where the data is being stored
     * @param columnsToUpdate columns to update from the data instance
     * @param key             column to look for the data to update
     * @param value           value to look for in the key column
     * @param data            instance of the data to be updated in the database
     * @return see SQL.execute() for more info
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     * @throws NoSuchFieldException     Issue with collecting the data from the object instance, via reflection
     * @see PreparedStatement#execute()
     */
    protected static <T> boolean update(String table, String[] columnsToUpdate, String key, String value, T data) throws SQLException, NoSuchFieldException, IllegalAccessException {
        PreparedStatement statement = connection.createPrepared("UPDATE `" + table + "` SET " + argumentGenerator(columnsToUpdate.length, 1, columnsToUpdate) + " WHERE " + key + "=?;");
        addArguments(statement, columnsToUpdate, data);
        statement.setString(columnsToUpdate.length + 1, value);
        LOG.trace("UPDATE: " + statement);
        return statement.execute();
    }

    /**
     * Update's existing data in the database
     *
     * @param table           table where the data is being stored
     * @param columnsToUpdate columns to update from the data instance
     * @param key             column to look for the data to update
     * @param value           value to look for in the key column
     * @param data            instance of the data to be updated in the database
     * @return see SQL.execute() for more info
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     * @throws NoSuchFieldException     Issue with collecting the data from the object instance, via reflection
     * @see PreparedStatement#execute()
     */
    protected static <T> boolean update(String table, String[] columnsToUpdate, String[] key, String[] value, T data) throws SQLException, NoSuchFieldException, IllegalAccessException {
        StringBuilder sql = new StringBuilder("UPDATE `" + table + "` SET " + argumentGenerator(columnsToUpdate.length, 1, columnsToUpdate) + " WHERE ");
        for (int x = 0; x < key.length; x++)
            sql.append(key[x]).append("=? ").append("AND ");
       String slq = sql.substring(0, sql.length() - 4).toString();
        PreparedStatement statement = connection.createPrepared(slq + ";");
        addArguments(statement, columnsToUpdate, data);
        for (int x = 1; x < key.length + 1; x++)
            statement.setString(columnsToUpdate.length + x, value[x - 1]);
        LOG.trace("UPDATE: " + statement);
        return statement.execute();
    }

    /**
     * Delete the given account from the database
     *
     * @param table table where the data is stored
     * @param key   column to delete the data instance from
     * @param value column / key value to look for the data object
     * @return see SQL.execute() for more info
     * @throws SQLException A SQL Error has occurred while running the request
     * @see PreparedStatement#execute()
     */
    protected static boolean delete(String table, String key, String value) throws SQLException {
        PreparedStatement statement = connection.createPrepared("DELETE FROM " + table + " WHERE " + key + "=?;");
        statement.setString(1, value);
        LOG.trace("DELETE: " + statement);
        return statement.execute();
    }

    /**
     * Reflection casts a ResultSet from the database into a usable data object
     *
     * @param result   ResultSet provided from the database.
     * @param dataType data instance to collect the data for
     * @return instance of the provided instance with the data from the database
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     */
    protected static <T> T to(ResultSet result, T dataType, boolean next) throws SQLException, IllegalAccessException, IllegalArgumentException {
        if (!next || result.next()) {
            for (Field field : dataType.getClass().getDeclaredFields()) {
                Object obj = result.getObject(field.getName());
                Class<?> fieldType = field.getType();
                boolean isArray = fieldType.isArray();
                boolean str = obj instanceof String;
                if (str && isArray && fieldType.equals(String[].class)) {
                    String[] data = ((String) obj).split(",");
                    for (int index = 0; index < data.length; index++)
                        data[index] = data[index].trim();
                    field.set(dataType, data);
                } else if (str && fieldType.equals(long.class) || str && fieldType.equals(Long.class))
                    field.set(dataType, Long.parseLong((String) obj));
                else if (str && fieldType.equals(int.class) || str && fieldType.equals(Integer.class))
                    field.set(dataType, Integer.parseInt((String) obj));
                else if (str && fieldType.equals(float.class) || str && fieldType.equals(Float.class))
                    field.set(dataType, Float.parseFloat((String) obj));
                else if (str && fieldType.equals(double.class) || str && fieldType.equals(Double.class) || str && fieldType.equals(BigDecimal.class))
                    field.set(dataType, Double.parseDouble((String) obj));
                else if (str && isJson(fieldType))
                    field.set(dataType, GSON.fromJson((String) obj, fieldType));
                else if (obj instanceof BigDecimal && fieldType.equals(Double.class))
                    field.set(dataType, ((BigDecimal) obj).doubleValue());
                else if (fieldType.equals(Long.class)) {
                    long data = (long) obj;
                    field.set(dataType, (long) data);
                } else if (fieldType.equals(Integer.class)) {
                    int data = (int) obj;
                    field.set(dataType, data);
                } else
                    field.set(dataType, obj);
            }
            return dataType;
        } else
            return null;
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
     * @param result   ResultSet provided from the database.
     * @param dataType data instance to collect the data for
     * @return instance of the provided instance with the data from the database
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     * @throws InstantiationException   Issue with reflection trying to create a new object instance
     */
    protected static <T> List<T> toArray(ResultSet result, T dataType) throws SQLException, IllegalAccessException, IllegalArgumentException, InstantiationException {
        List<T> dataArr = new ArrayList<>();
        while (result.next()) {
            // Attempt to create new instance and set values
            T data = (T) dataType.getClass().newInstance();
            T temp = to(result, data, false);
            if (temp != null)
                dataArr.add(temp);
        }
        return dataArr;
    }

    /**
     * Generates the string for use with SQLGenerator params, based on the amount needed and its columns
     *
     * @param count   amount of arguments to generate
     * @param format  format of the arguments to generate
     * @param columns list of columns in the table
     * @return a pre-formatted string for use within a sql statement using params
     */
    private static String argumentGenerator(int count, int format, String[] columns) {
        if (count > 0)
            if (format == 0) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < count; x++)
                    builder.append("?, ");
                return builder.substring(0, builder.length() - 2);
            } else if (format == 1 && columns != null && columns.length > 0) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < count; x++)
                    builder.append(columns[x]).append(" = ?, ");
                return builder.substring(0, builder.length() - 2);
            }
        return "";
    }

    /**
     * Adds the parameters / arguments into a sql statement, collected from a data instance
     *
     * @param pStatement sql statement to add the arguments onto
     * @param columns    columns of the data for the SQL request
     * @param data       data to collect the data to be used for the params
     * @throws SQLException           A SQL Error has occurred while running the request
     * @throws NoSuchFieldException   Issue with reflection to get data from the object instance
     * @throws IllegalAccessException Issue with reflection to get data from the object instance
     */
    private static <T> PreparedStatement addArguments(PreparedStatement pStatement, String[] columns, T data) throws SQLException, NoSuchFieldException, IllegalAccessException {
        for (int index = 0; index < columns.length; index++) {
            Object fieldData = data.getClass().getDeclaredField(columns[index]).get(data);
            // Ignore null entries
            if (fieldData == null)
                continue;
            // Check for Special Cases
            if (fieldData instanceof String[]) {
                pStatement.setObject(index + 1, ((String[]) fieldData).length > 0 ? Strings.join(((String[]) fieldData), ", ") : " ");
                continue;
            }
            if (fieldData instanceof SQLJson[] || fieldData instanceof SQLJson) {
                pStatement.setObject(index + 1, GSON.toJson(fieldData).replaceAll("\n", "").replaceAll(" ", ""));
                continue;
            }
            // Not a special case
            pStatement.setObject(index + 1, fieldData);
        }
        return pStatement;
    }

    /**
     * Mimics how a database update is completed, without the need to request the update from the database
     *
     * @param columnsToUpdate columns in the database that have been updated
     * @param updateData      data that was used to update the database
     * @param localInfo       data from the database, before the update
     * @return Updated version of the data, (should be in-sync with the database)
     * @throws NoSuchFieldException   Issue with reflection to collect data from the object instance
     * @throws IllegalAccessException Issue with reflection to collect data from the object instance
     */
    protected static <T> T updateInfoLocal(String[] columnsToUpdate, T updateData, T localInfo) throws NoSuchFieldException, IllegalAccessException {
        for (String column : columnsToUpdate) {
            Field field = updateData.getClass().getDeclaredField(column);
            field.set(localInfo, field.get(updateData));
        }
        return localInfo;
    }
}
