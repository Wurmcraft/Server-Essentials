package io.wurmatron.serveressentials.sql;

import java.sql.*;

import static io.wurmatron.serveressentials.ServerEssentialsRest.config;

public class DatabaseConnection {

    private final Connection connection;

    public DatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(createConnectionURL(), config.database.username, config.database.password);
        DatabasePopulator.setupDB(connection);
    }

    private static String createConnectionURL() {
        return "jdbc:" + config.database.connector + "://" +
                config.database.host + ":" +
                config.database.port + "/" +
                config.database.database + "?" +
                config.database.sqlParams;
    }

    public Statement create() throws SQLException {
        return connection.createStatement();
    }

    public PreparedStatement createPrepared(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public PreparedStatement createPrepared(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }
}
