package io.wurmatron.serveressentials.tests.sql;


import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.account.BankAccount;
import io.wurmatron.serveressentials.models.account.ServerTime;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAccount;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAccounts {

    public static final Account TEST_ACCOUNT = new Account("d8d5a923-7b20-43d8-883b-1150148d6955",
            "Test", new String[]{"Debug", "Debug2"}, new String[]{"general.test"}, new String[]{"home.amount.5"},
            "en_us", false, 0, "Test", "",
            new ServerTime[]{new ServerTime("test", 9001, Instant.now().toEpochMilli())},
            new BankAccount[]{new BankAccount("Test", 50, Instant.now().toEpochMilli(), "Savings", "{}", Instant.now().toEpochMilli())},
            0, "", "", new String[]{});

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddAccount() {
        // Check if account exists, if so delete it
        if (SQLCacheAccount.get(TEST_ACCOUNT.uuid) != null)
            SQLCacheAccount.deleteAccount(TEST_ACCOUNT.uuid);
        // Add new account
        Account account = SQLCacheAccount.newAccount(TEST_ACCOUNT);
        assertNotNull(account, "Account has been successfully created without errors");
        // Check for the new account
        Account savedAccount = SQLCacheAccount.get(TEST_ACCOUNT.uuid);
        assertEquals(TEST_ACCOUNT, savedAccount, "Added account should be the same as the one saved.");
    }

    @Test
    @Order(2)
    public void testGetAccount() {
        Account account = SQLCacheAccount.get(TEST_ACCOUNT.uuid);
        assertEquals(TEST_ACCOUNT, account, "The Accounts are equal");
        // Remove from cache and try again
        SQLCacheAccount.invalidate(TEST_ACCOUNT.uuid);
        account = SQLCacheAccount.get(TEST_ACCOUNT.uuid);
        // Test Account
        assertNotNull(account, "Cache has the account");
        assertEquals(TEST_ACCOUNT.uuid, account.uuid, "UUID's are the same");
        assertEquals(TEST_ACCOUNT.rewardPoints, account.rewardPoints, "Reward Point's are the same");
        assertArrayEquals(TEST_ACCOUNT.rank, account.rank, "Ranks are the same");
        assertArrayEquals(TEST_ACCOUNT.trackedTime, account.trackedTime, "Tracked Time is the same");
        assertArrayEquals(TEST_ACCOUNT.wallet, account.wallet, "Wallet is the same");
    }

    @Test
    @Order(2)
    public void testUpdateAccount() {
        TEST_ACCOUNT.language = "en_us";
        boolean updated = SQLCacheAccount.updateAccount(TEST_ACCOUNT, new String[]{"language"});
        assertTrue(updated, "Account has been successfully updated without errors");
        // Check for updates
        Account account = SQLCacheAccount.get(TEST_ACCOUNT.uuid);
        assertNotNull(account, "Account Exists");
        assertEquals(TEST_ACCOUNT.language, account.language, "Language has been updated");
        // Invalidate Cache and try again
        SQLCacheAccount.invalidate(TEST_ACCOUNT.uuid);
        account = SQLCacheAccount.get(TEST_ACCOUNT.uuid);
        assertNotNull(account, "Account Exists");
        assertEquals(TEST_ACCOUNT.language, account.language, "Language has been updated");
    }

    @Test
    @Order(3)
    public void testDeleteAccount() {
        boolean deleted = SQLCacheAccount.deleteAccount(TEST_ACCOUNT.uuid);
        assertTrue(deleted, "Account has been successfully deleted without errors");
        // Make sure it was deleted
        Account account = SQLCacheAccount.get(TEST_ACCOUNT.uuid);
        assertNull(account, "Account does not exist");
        // Invalidate Cache and try again
        SQLCacheAccount.invalidate(TEST_ACCOUNT.uuid);
        account = SQLCacheAccount.get(TEST_ACCOUNT.uuid);
        assertNull(account, "Account does not exist");
    }

    @Test
    @Order(1)
    public void checkForMiscAccount() {
        Account account = SQLCacheAccount.get("fc074365-07af-43a5-bd37-c780b6b8a497"); // Valid UUID, Invalid Name
        assertNull(account, "Invalid account should be null");
    }
}
