package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.sql.SQLGenerator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
     */
    public static <T> List<T> queryArray(String sql, T dataType) throws SQLException, IllegalAccessException, IllegalArgumentException, InstantiationException {
        PreparedStatement statement = connection.createPrepared(sql);
        LOG.trace("DIRECT ARR: " + statement);
        return toArray(statement.executeQuery(), dataType);
    }

    /**
     * Directly execute a sql statement
     *
     * @param sql sql statment to be run
     * @return  resulting info from that sql server
     * @throws SQLException unable to create the sql statement
     */
    public static ResultSet direct(String sql) throws SQLException {
        LOG.trace("DIRECT SQL: " + sql);
        return connection.create().executeQuery(sql);
    }
}
