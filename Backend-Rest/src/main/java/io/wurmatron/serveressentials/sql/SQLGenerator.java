package io.wurmatron.serveressentials.sql;

import joptsimple.internal.Strings;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

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

    private final DatabaseConnection connection;

    public SQLGenerator(DatabaseConnection connection) {
        this.connection = connection;
    }

    protected <T> T get(String columns, String table, String key, String data, T dataType) throws SQLException, IllegalAccessException, IllegalArgumentException {
        PreparedStatement statement = connection.createPrepared("SELECT " + columns + " FROM `" + table + "` WHERE " + key + "=?;");
        statement.setString(1, data);
        LOG.trace("GET: " + statement);
        return to(statement.executeQuery(), dataType);
    }

    protected <T> boolean insert(String table, String[] columns, T data) throws SQLException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        PreparedStatement statement = connection.createPrepared("INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES (" + argumentGenerator(columns.length - 1, 0, null) + ");");
        addArguments(statement, columns, data);
        LOG.trace("INSERT: " + statement);
        return statement.execute();
    }

    protected <T> boolean update(String table, String[] columnsToUpdate, String key, String value, T data) throws SQLException, NoSuchFieldException, IllegalAccessException {
        PreparedStatement statement = connection.createPrepared("UPDATE " + table + " SET " + argumentGenerator(columnsToUpdate.length - 1, 1, columnsToUpdate) + " WHERE " + key + "=?;");
        addArguments(statement, columnsToUpdate, data);
        statement.setString(columnsToUpdate.length, value);
        LOG.trace("UPDATE: " + statement);
        return statement.execute();
    }

    protected <T> boolean delete(String table, String key, String value) throws SQLException {
        PreparedStatement statement = connection.createPrepared("DELETE FROM " + table + " WHERE " + key + "=?;");
        statement.setString(1, value);
        LOG.trace("DELETE: " + statement);
        return statement.execute();
    }


    private <T> T to(ResultSet result, T dataType) throws SQLException, IllegalAccessException, IllegalArgumentException {
        if (result.next())
            for (Field field : dataType.getClass().getDeclaredFields())
                field.set(dataType, result.getObject(field.getName()));
        return dataType;
    }

    private String argumentGenerator(int count, int type, String[] columns) {
        if (count > 0)
            if (type == 0) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < count; x++)
                    builder.append("?, ");
                return builder.substring(0, builder.length() - 2);
            } else if (type == 1 && columns != null && columns.length > 0) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < count; x++)
                    builder.append(columns[x]).append(" = ?, ");
                return builder.substring(0, builder.length() - 2);
            }
        return "";
    }

    private <T> void addArguments(PreparedStatement pStatment, String[] columns, T data) throws NoSuchFieldException, IllegalAccessException, SQLException {
        for (int index = 0; index < columns.length; index++) {
            Object fieldData = data.getClass().getDeclaredField(columns[index]).get(data);
            // Ignore null entries
            if (fieldData == null)
                continue;
            // Check for Special Cases
            if (fieldData instanceof String[]) {
                pStatment.setObject(index + 1, Strings.join(((String[]) fieldData), ", "));
                continue;
            }
            // Not a special case
            pStatment.setObject(index + 1, fieldData);
        }
    }
}
