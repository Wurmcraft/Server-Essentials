package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Ban;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheBan;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBans {

    public static final Ban TEST_BAN = new Ban(-1, TestAccounts.TEST_ACCOUNT.uuid,"localhost", "", "148", "Minecraft", "Testing", Instant.now().getEpochSecond(), "PERM", "{}", true);

    @BeforeAll
    public static void setup() throws Exception {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddBan() {
        Ban ban = SQLCacheBan.create(TEST_BAN);
        assertNotNull(ban, "Ban has been created successfully without errors");
        // Check for new ban
        TEST_BAN.ban_id = ban.ban_id;
        ban = SQLCacheBan.get(ban.ban_id);
        assertEquals(TEST_BAN, ban, "Added ban should be the same as existing");
    }

    @Test
    @Order(2)
    public void testGetBan() {
        Ban ban = SQLCacheBan.get(TEST_BAN.ban_id);
        assertEquals(TEST_BAN, ban, "The ban's are equal");
        // Remove from cache and try again
        SQLCacheBan.invalidate(ban.ban_id);
        ban = SQLCacheBan.get(ban.ban_id);
        // Test Ban
        assertNotNull(ban, "Cache has the ban");
        assertEquals(TEST_BAN, ban, "Ban instances are the same");
    }

    @Test
    @Order(2)
    public void testUpdateBan() {
        TEST_BAN.banned_by = TestAccounts.TEST_ACCOUNT.uuid;
        boolean updated = SQLCacheBan.update(TEST_BAN, new String[]{"bannedBy"});
        assertTrue(updated, "Ban has been successfully updated without errors");
        // Check for updates
        Ban ban = SQLCacheBan.get(TEST_BAN.ban_id);
         assertNotNull(ban, "Ban exists");
        assertEquals(TEST_BAN.banned_by, ban.banned_by, "Banned-By has been updated");
        // Invalidate Cache and try again
        SQLCacheBan.invalidate(TEST_BAN.ban_id);
        ban = SQLCacheBan.get(TEST_BAN.ban_id);
        assertNotNull(ban, "Ban Exists");
        assertEquals(TEST_BAN.banned_by, ban.banned_by, "Banned-By has been updated");
    }

    @Test
    @Order(2)
    public void testGetBans() {
        List<Ban> bans = SQLCacheBan.get();
        assertTrue(bans.size() > 0, "Ban exist");
        // Remove from cache and try again
        for (Ban ban : bans)
            SQLCacheBan.invalidate(ban.ban_id);
        bans = SQLCacheBan.get();
        assertTrue(bans.size() > 0, "Ban exist");
        boolean found = false;
        for (Ban ban : bans)
            if (ban.equals(TEST_BAN)) {
                found = true;
                break;
            }
        assertTrue(found, "Added Ban exists");
    }

    @Test
    @Order(3)
    public void testDeleteBan() {
        boolean deleted = SQLCacheBan.delete(TEST_BAN.ban_id);
        assertTrue(deleted, "Ban has been successfully deleted without errors");
        // Make sure it was deleted
        Ban ban = SQLCacheBan.get(TEST_BAN.ban_id);
        assertNull(ban, "Ban does not exist");
        // Invalidate Cache and try again
        SQLCacheBan.invalidate(TEST_BAN.ban_id);
        ban = SQLCacheBan.get(TEST_BAN.ban_id);
        assertNull(ban, "Ban does not exist");
    }
}
