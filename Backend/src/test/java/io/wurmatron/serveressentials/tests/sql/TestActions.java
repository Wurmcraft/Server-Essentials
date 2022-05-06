package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Action;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLActions;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestActions {

    public static final Action TEST_ACTION = new Action(TestAccounts.TEST_ACCOUNT.uuid, "Test", "Create", "{}", ""+Instant.now().getEpochSecond());

    @BeforeAll
    public static void setup() throws Exception {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddAction() {
        Action action = SQLActions.create(TEST_ACTION);
        assertNotNull(action, "Action has been created sucessfully without errors");
        // Check for new action
        List<Action> actions = SQLActions.get(TEST_ACTION.related_id, TEST_ACTION.action);
        boolean found = false;
        for (Action a : actions)
            if (TEST_ACTION.equals(a)) {
                found = true;
                break;
            }
        assertTrue(found, "Action was added successfully");
    }

    @Test
    @Order(2)
    public void testUpdateAction() {
        TEST_ACTION.action_data = "{\"type\": \"Test2\"}";
        Action action = SQLActions.update(TEST_ACTION, new String[] {"action_data"});
        assertNotNull(action, "Action has updated successfully without errors");
        // Make sure it was updated
        List<Action> actions = SQLActions.get(TEST_ACTION.related_id, TEST_ACTION.action);
        boolean found = false;
        for (Action a : actions)
            if(a.action_data.equals(TestActions.TEST_ACTION.action_data))
                found = true;
        assertTrue(found, "Action was updated successfully");
    }

    @Test
    @Order(2)
    public void testGetAction() {
        List<Action> actions = SQLActions.get(TEST_ACTION.related_id);
        assertNotNull(actions);
    }

    @Test
    @Order(3)
    public void testDeleteAction() {
        Action deletedAction = SQLActions.delete(TEST_ACTION.host, TEST_ACTION.action, TEST_ACTION.related_id, TEST_ACTION.timestamp);
        assertNotNull(deletedAction, "Action was deleted successfully without errors");
        // Make sure it was deleted
        List<Action> actions = SQLActions.get(TEST_ACTION.related_id);
        boolean found = false;
        for (Action a : actions)
            if (TEST_ACTION.equals(a)) {
                found = true;
                break;
            }
        assertFalse(found, "Deleted Action was not found");
    }
}
