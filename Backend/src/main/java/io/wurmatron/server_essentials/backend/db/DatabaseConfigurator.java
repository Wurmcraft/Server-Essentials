/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.db;

import java.util.HashMap;

/**
 * Helps with the configuration and creation of the database along with ongoing maintenance such as table updates.
 */
public class DatabaseConfigurator {

    /**
     * TODO Implement
     *
     * Configure the initial DB connection and settings.
     *
     * @param autofillValues values to be used by automatic setup for autofill into setup prompts
     */
    public static boolean initialDBSetup(HashMap<String, String> autofillValues) {
        return false;
    }

    /**
     * Set up the defaults for each table
     */
    public static void setupInitialDefaults() {

    }

    /**
     * Create the Tables for use in the database
     */
    public static void createTables() {

    }

    /**
     * Check if existing tables need updates and if so update them accordingly
     */
    public static void updateExistingTables() {

    }
}
