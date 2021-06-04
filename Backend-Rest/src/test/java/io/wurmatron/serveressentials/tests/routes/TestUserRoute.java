package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.account.BankAccount;
import io.wurmatron.serveressentials.models.account.ServerTime;
import io.wurmatron.serveressentials.tests.sql.TestAccounts;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.SQLException;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUserRoute {

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
    public void testAddUser() throws IOException {
        Account createdAccount = HTTPRequests.postWithReturn("api/user", TestAccounts.TEST_ACCOUNT, Account.class);
        assertNotNull(createdAccount, "Account is not null");
        assertEquals(TestAccounts.TEST_ACCOUNT, createdAccount);
        // Check if account was added
        createdAccount = HTTPRequests.get("api/user/" + createdAccount.uuid, Account.class);
        assertNotNull(createdAccount, "Account is not null");
        assertEquals(TestAccounts.TEST_ACCOUNT, createdAccount);
    }

    @Test
    @Order(2)
    public void testGetUser() throws IOException {
        Account account = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid, Account.class);
        assertNotNull(account, "Account is not null");
        assertEquals(TestAccounts.TEST_ACCOUNT, account);
    }

    @Test
    @Order(2)
    public void testGetUsername() throws IOException {
        String username = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.username + "/username", Account.class).username;
        assertEquals(TestAccounts.TEST_ACCOUNT.username, username, "Username is the same");
    }

    @Test
    @Order(2)
    public void testGetRanks() throws IOException {
        String[] ranks = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/rank", Account.class).perks;
        assertEquals(TestAccounts.TEST_ACCOUNT.rank, ranks, "Ranks are the same");
    }

    @Test
    @Order(2)
    public void testGetPerms() throws IOException {
        String[] perms = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/perms", Account.class).perms;
        assertEquals(TestAccounts.TEST_ACCOUNT.perms, perms, "Perms are the same");
    }

    @Test
    @Order(2)
    public void testGetPerks() throws IOException {
        String[] perks = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/perks", Account.class).perks;
        assertEquals(TestAccounts.TEST_ACCOUNT.perks, perks, "Perks are the same");
    }

    @Test
    @Order(2)
    public void testGetLanguage() throws IOException {
        String language = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/language", Account.class).language;
        assertEquals(TestAccounts.TEST_ACCOUNT.language, language, "Language is the same");
    }

    @Test
    @Order(2)
    public void testGetMuted() throws IOException {
        boolean muted = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/muted", Account.class).muted;
        assertEquals(TestAccounts.TEST_ACCOUNT.muted, muted, "Muted is the same");
    }

    @Test
    @Order(2)
    public void testGetMutedTime() throws IOException {
        long muteTime = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/mute-time", Account.class).muteTime;
        assertEquals(TestAccounts.TEST_ACCOUNT.muteTime, muteTime, "Mute Time is the same");
    }

    @Test
    @Order(2)
    public void testGetDisplayName() throws IOException {
        String displayName = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/display-name", Account.class).displayName;
        assertEquals(TestAccounts.TEST_ACCOUNT.displayName, displayName, "DisplayName is the same");
    }

    @Test
    @Order(2)
    public void testGetDiscordID() throws IOException {
        String discordID = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/discord-id", Account.class).discordID;
        assertEquals(TestAccounts.TEST_ACCOUNT.discordID, discordID, "DiscordID is the same");
    }

    @Test
    @Order(2)
    public void testGetTrackedTime() throws IOException {
        ServerTime[] playTime = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/playtime", Account.class).trackedTime;
        assertEquals(TestAccounts.TEST_ACCOUNT.trackedTime, playTime, "Tracked-Time is the same");
    }

    @Test
    @Order(2)
    public void testGetWallet() throws IOException {
        BankAccount[] banks = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/wallet", Account.class).wallet;
        assertEquals(TestAccounts.TEST_ACCOUNT.wallet, banks, "Wallet is the same");
    }

    @Test
    @Order(2)
    public void testGetRewardPoints() throws IOException {
        int points = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/reward-points", Account.class).rewardPoints;
        assertEquals(TestAccounts.TEST_ACCOUNT.rewardPoints, points, "Reward Points are the same");
    }

    @Test
    @Order(2)
    public void testSystemPerms() throws IOException {
        String[] systemPerms = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/system-perms", Account.class).systemPerms;
        assertEquals(TestAccounts.TEST_ACCOUNT.systemPerms, systemPerms, "System Perms are the same");
    }

    @Test
    @Order(2)
    public void testFullUpdate() throws IOException {
        TestAccounts.TEST_ACCOUNT.displayName = "TestFullUpdate";
        int status = HTTPRequests.put("api/user/" + TestAccounts.TEST_ACCOUNT.uuid, TestAccounts.TEST_ACCOUNT);
        assertEquals(HttpURLConnection.HTTP_OK, status, "Account has been updated");
        // Make sure its updated
        Account updatedAccount = HTTPRequests.get("api/user", Account.class);
        assertEquals(TestAccounts.TEST_ACCOUNT, updatedAccount, "Updated account are equal");
    }

    @Test
    @Order(2)
    public void testPatch() throws IOException {
        TestAccounts.TEST_ACCOUNT.language = "en_au";
        int status = HTTPRequests.patch("api/user/" + TestAccounts.TEST_ACCOUNT.uuid + "/language", TestAccounts.TEST_ACCOUNT);
        assertEquals(HttpURLConnection.HTTP_OK, status, "Account has been updated");
        // Make sure its updated
        String updatedLang = HTTPRequests.get("api/user", Account.class).language;
        assertEquals(TestAccounts.TEST_ACCOUNT.language, updatedLang, "Language has been updated / changed");
    }

    // TODO Test for /user #GetAccounts

    @Test
    @Order(3)
    public void testDeleteUser() throws IOException {
        Account deletedAccount = HTTPRequests.deleteWithReturn("api/user/" + TestAccounts.TEST_ACCOUNT.uuid, null, Account.class);
        assertNotNull(deletedAccount, "Account should be returned");
        // Make sure its been deleted
        deletedAccount = HTTPRequests.get("api/user/" + TestAccounts.TEST_ACCOUNT.uuid, Account.class);
        assertNull(deletedAccount, "Account has bee deleted");
    }
}
