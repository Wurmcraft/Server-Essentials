package io.wurmatron.serveressentials.sql;

import joptsimple.internal.Strings;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    private static DatabaseConnection connection;

    public static DatabaseConnection create() {
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
        return to(statement.executeQuery(), dataType);
    }

    /**
     * Insert new data into the database
     *
     * @param table   table to insert this data into
     * @param columns columns you want to insert the data into
     * @param data    instance of the data to send to the database
     * @return see SQL.execute() for more info
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     * @throws NoSuchFieldException     Issue with collecting the data from the object instance, via reflection
     * @see PreparedStatement#execute()
     */
    protected static <T> boolean insert(String table, String[] columns, T data) throws SQLException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        PreparedStatement statement = connection.createPrepared("INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES (" + argumentGenerator(columns.length - 1, 0, null) + ");");
        addArguments(statement, columns, data);
        LOG.trace("INSERT: " + statement);
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
    protected static <T> boolean update(String table, String[] columnsToUpdate, String key, String value, T data) throws SQLException, NoSuchFieldException, IllegalAccessException {
        PreparedStatement statement = connection.createPrepared("UPDATE " + table + " SET " + argumentGenerator(columnsToUpdate.length - 1, 1, columnsToUpdate) + " WHERE " + key + "=?;");
        addArguments(statement, columnsToUpdate, data);
        statement.setString(columnsToUpdate.length, value);
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
    private static <T> T to(ResultSet result, T dataType) throws SQLException, IllegalAccessException, IllegalArgumentException {
        if (result.next())
            for (Field field : dataType.getClass().getDeclaredFields())
                field.set(dataType, result.getObject(field.getName()));
        return dataType;
    }

    /**
     * Generates the string for use with sql params, based on the amount needed and its columns
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
     * @param columns    columns of the data for the sql request
     * @param data       data to collect the data to be used for the params
     * @throws SQLException           A SQL Error has occurred while running the request
     * @throws NoSuchFieldException   Issue with reflection to get data from the object instance
     * @throws IllegalAccessException Issue with reflection to get data from the object instance
     */
    private static <T> void addArguments(PreparedStatement pStatement, String[] columns, T data) throws SQLException, NoSuchFieldException, IllegalAccessException {
        for (int index = 0; index < columns.length; index++) {
            Object fieldData = data.getClass().getDeclaredField(columns[index]).get(data);
            // Ignore null entries
            if (fieldData == null)
                continue;
            // Check for Special Cases
            if (fieldData instanceof String[]) {
                pStatement.setObject(index + 1, Strings.join(((String[]) fieldData), ", "));
                continue;
            }
            // Not a special case
            pStatement.setObject(index + 1, fieldData);
        }
    }
}
