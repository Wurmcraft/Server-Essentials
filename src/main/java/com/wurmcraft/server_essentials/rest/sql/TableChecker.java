package com.wurmcraft.server_essentials.rest.sql;

import java.sql.SQLException;
import java.sql.Statement;

import static com.wurmcraft.server_essentials.rest.SE_Rest.LOG;
import static com.wurmcraft.server_essentials.rest.SE_Rest.connector;

public class TableChecker {

    public static boolean hasSETables() {
        try {
            Statement statement = connector.getConnection().createStatement();
            statement.execute("SELECT * from `server-essentials`.users");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public static void createIfMissing() {
        if (!hasSETables()) {
            try {
                Statement statement = connector.getConnection().createStatement();
                statement.execute("CREATE TABLE `users` (`uuid` text NOT NULL, `rank` text NOT NULL,`username` text NOT NULL);");
                statement.execute("ALTER TABLE `users` ADD PRIMARY KEY (`uuid`(36))");
                LOG.info("Adding users table");
            } catch (SQLException e) {
                LOG.error(e.getMessage());
            }
        }
    }
}
