/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.backend.db;

import io.wurmatron.server_essentials.backend.db.DatabaseConnector;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDatabaseConnector {

    @Test
    @Order(1)
    public void TestDatabaseConnection() {
        SessionFactory factory = DatabaseConnector.getSession();
        assertNotNull(factory, "DB Session is not null");
    }
}
