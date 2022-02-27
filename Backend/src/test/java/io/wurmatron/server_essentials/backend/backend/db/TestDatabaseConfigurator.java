/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.backend.db;

import io.wurmatron.server_essentials.backend.db.DatabaseConfigurator;
import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDatabaseConfigurator {

    @AfterAll
    public static void setup() {
        // TODO Delete the database
    }

    @Test
    @Order(1)
    public void TestInitialSetup() {
        HashMap<String, String> autofillValues = new HashMap<>();
        // TODO Values
        assertTrue(DatabaseConfigurator.initialDBSetup(autofillValues), "DB was setup without errors");
    }

    @Test
    @Order(2)
    public void TestDatabase_Database() {
        // TODO Check if table exists
        boolean exists = false;
        assertTrue(exists, "Database exists");
    }

    @Test
    @Order(3)
    public void TestTable_Creation() {
        DatabaseConfigurator.createTables();
        // TODO Check if tables exist
        boolean tablesExist = false;
        assertTrue(tablesExist, "Tables exists");
    }

    @Test
    @Order(4)
    public void TestDefaults() {
        DatabaseConfigurator.setupInitialDefaults();
        boolean defaultsExist = false;
        assertTrue(defaultsExist, "Table Defaults exist");
    }

    @Test
    @Order(4)
    public void TestUpdateTables() {
        // TODO Delete Column
        DatabaseConfigurator.updateExistingTables();
        boolean tablesEqual = false;
        assertTrue(tablesEqual, "Table has been updated / fixed");
    }
}
