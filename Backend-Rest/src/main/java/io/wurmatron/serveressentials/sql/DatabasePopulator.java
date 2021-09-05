package io.wurmatron.serveressentials.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.stream.Collectors;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.ServerEssentialsRest.config;

public class DatabasePopulator {

    public static String[] tables = {"actions", "autoranks", "bans", "currencys", "donator", "logging", "markets", "ranks", "statistics", "transfers", "users"};

    /**
     * Checks if all the tables exist within the sql database if not they are created
     */
    public static void setupDB(Connection c) {
        for (String table : tables)
            if (!checkIfExists(c, table)) {
                LOG.info("SQL Table '" + table + "' does not exist!, Creating... ");
                createTable(c, table);
            }
    }

    /**
     * Check if a table exists
     *
     * @param tableName name of the table to check if exists
     * @return if the table exists
     */
    private static boolean checkIfExists(Connection c, String tableName) {
        try {
            String sqlStatment = "";
            if (config.database.connector.equalsIgnoreCase("mysql"))
                 sqlStatment = "SELECT * FROM information_schema.tables where table_name='" + tableName + "' AND table_schema='" + ServerEssentialsRest.config.database.database + "'";
            else if(config.database.connector.equalsIgnoreCase("postgresql")) {
                sqlStatment = "SELECT * FROM information_schema.tables where table_name='" + tableName + "' AND table_schema='public'";
            }
            ResultSet set = c.createStatement().executeQuery(sqlStatment);
            if (!set.next())
                return false;
        } catch (Exception e) {
            LOG.debug("Failed to check if table exists ' " + tableName + "' (" + e.getMessage() + ")");
        }
        return true;
    }

    /**
     * Creates a table based on its name, used to lookup setup sql file
     *
     * @param tableName name of the table to be created
     */
    public static void createTable(Connection c, String tableName) {
        String tableSQL = readSQLSetupFile("sql" + File.separator + config.database.connector + File.separator + tableName + ".sql");
        try {
            c.createStatement().execute(tableSQL);
            LOG.info("Table '" + tableName + "' Created!");
        } catch (Exception e) {
            LOG.warn("Failed to create table '" + tableName + "' (" + e.getMessage() + ")");
        }
    }

    /**
     * Reads the provided resource into a string
     *
     * @param fileName name of the file to read
     * @return the file read into a single string
     */
    private static String readSQLSetupFile(String fileName) {
        InputStream in = DatabasePopulator.class.getClassLoader().getResourceAsStream(fileName);
        if (in != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines().collect(Collectors.joining());
        }
        return "";
    }
}
