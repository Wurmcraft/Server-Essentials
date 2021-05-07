package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.sql.SQLGenerator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class SQLDirect extends SQLGenerator {

    /**
     * Send a Query to the SQL Server
     *
     * @param <T>      type of data to be returned
     * @param sql      statement to query
     * @param dataType expect data type returned
     * @throws SQLException             A SQL Error has occurred while running the request
     * @throws IllegalAccessException   Issue with reflection to add data to the object instance
     * @throws IllegalArgumentException Issue with reflection to add data to the object instance
     * @return
     */
    public static <T> List<T> queryArray(String sql, T dataType) throws SQLException, IllegalAccessException, IllegalArgumentException, InstantiationException {
        PreparedStatement statement = connection.createPrepared(sql);
        LOG.trace("DIRECT ARR: " + statement);
        return toArray(statement.executeQuery(), dataType);
    }
}
