package io.wurmatron.serveressentials.sql;

import java.sql.*;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.ServerEssentialsRest.config;

public class DatabaseConnection {

    private Connection connection;

    public DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(createConnectionURL(), config.database.username, config.database.password);
        } catch (SQLException e)  {
            LOG.error("Failed to connect to SQL '" + createConnectionURL() + "'");
            e.printStackTrace();
            LOG.error(e.getMessage());
            System.exit(2);
        }
    }

    public Statement create() throws SQLException {
        return connection.createStatement();
    }

    public PreparedStatement createPrepared(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    private static String createConnectionURL() {
        return "jdbc:mysql://" +
                config.database.host + ":" +
                config.database.port + "/" +
                config.database.database + "?" +
                config.database.sqlParams;
    }
}
