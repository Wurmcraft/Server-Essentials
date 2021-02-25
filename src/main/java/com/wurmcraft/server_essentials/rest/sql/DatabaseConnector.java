package com.wurmcraft.server_essentials.rest.sql;

import com.wurmcraft.server_essentials.rest.config.RestConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {

    private final RestConfig.Database config;
    private Connection connection;

    public DatabaseConnector(RestConfig.Database config) {
        this.config = config;
        connect();
    }

    public boolean connect() {
        String url = "jdbc:mysql://" + config.address + ":" + config.port + "/" + config.database_name + "";
        try {
            connection = DriverManager.getConnection(url, config.username, config.password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public final Connection getConnection() {
        try {
            if (connection.isClosed()) {
                connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
