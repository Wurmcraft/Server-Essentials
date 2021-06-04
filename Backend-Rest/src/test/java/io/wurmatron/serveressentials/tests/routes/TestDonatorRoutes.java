package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Donator;
import io.wurmatron.serveressentials.tests.sql.TestDonators;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDonatorRoutes {

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        if(!isSetup) {
            ServerEssentialsRest.main(new String[]{});
            HTTPRequests.BASE_URL = "http://" + ServerEssentialsRest.config.server.host + ":" + ServerEssentialsRest.config.server.port + "/";
            // TODO Add Authentication or force config.testing when running tests
            isSetup = true;
        }
    }

    @Test
    @Order(1)
    public void testAddDonationEntry() throws IOException {
        Donator newEntry = HTTPRequests.postWithReturn("api/donator", TestDonators.TEST_DONATOR, Donator.class);
        assertNotNull(newEntry, "Entry was created successfully");
        // Make sure the donation entry was created
        Donator[] entries = HTTPRequests.get("api/donator?transaction=" + TestDonators.TEST_DONATOR.transactionID, Donator[].class);
        boolean exists = false;
        for (Donator entry : entries)
            if (TestDonators.TEST_DONATOR.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Donation Entry was added successfully");
    }

    @Test
    @Order(2)
    public void testGetDonationEntry() throws IOException {
        Donator[] entries = HTTPRequests.get("api/donator?uuid=" + TestDonators.TEST_DONATOR.uuid, Donator[].class);
        boolean exists = false;
        for (Donator entry : entries)
            if (TestDonators.TEST_DONATOR.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Donation Entry was found successfully");
    }

    @Test
    @Order(2)
    public void testUpdateDonationEntry() throws IOException {
        TestDonators.TEST_DONATOR.amount = 9000.1;
        HTTPRequests.put("api/donator", TestDonators.TEST_DONATOR);
        // Make sure the entry was updated successfully
        Donator[] entries = HTTPRequests.get("api/donator?type=" + TestDonators.TEST_DONATOR.type, Donator[].class);
        boolean exists = false;
        for (Donator entry : entries)
            if (TestDonators.TEST_DONATOR.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Donation Entry was updated successfully");
    }

    @Test
    @Order(3)
    public void testDeleteDonationEntry() throws IOException {
        Donator deletedEntry = HTTPRequests.deleteWithReturn("api/donator", TestDonators.TEST_DONATOR, Donator.class);
        assertNotNull(deletedEntry, "Entry was deleted successfully");
        //  Check if the entry was deleted successfully
        Donator[] entries = HTTPRequests.get("api/donator?type=" + TestDonators.TEST_DONATOR.type, Donator[].class);
        boolean exists = false;
        for (Donator entry : entries)
            if (TestDonators.TEST_DONATOR.equals(entry)) {
                exists = true;
                break;
            }
        assertFalse(exists, "Donation Entry was deleted successfully");
    }
}
