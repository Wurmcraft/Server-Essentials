package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Action;
import io.wurmatron.serveressentials.tests.sql.TestActions;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestActionRoutes {

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
    public void testAddAction() throws IOException {
        Action addedAction = HTTPRequests.postWithReturn("api/action", TestActions.TEST_ACTION, Action.class);
        assertNotNull(addedAction, "Action was created successfully");
        // Make sure the action was added successfully
        Action[] actions = HTTPRequests.get("api/action?action=" + addedAction.action, Action[].class);
        boolean exists = false;
        for (Action action : actions)
            if (TestActions.TEST_ACTION.equals(action)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Action was added successfully");
    }

    @Test
    @Order(2)
    public void testGetAction() throws IOException {
        Action[] actions = HTTPRequests.get("api/action?host=" + TestActions.TEST_ACTION.host, Action[].class);
        boolean exists = false;
        for (Action action : actions)
            if (TestActions.TEST_ACTION.equals(action)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Action was added successfully");
    }

    @Test
    @Order(2)
    public void testUpdateAction() throws IOException {
        TestActions.TEST_ACTION.action_data = "{\"Test\": true}";
        HTTPRequests.put("api/action", TestActions.TEST_ACTION);
        // Check if action was updated
        Action[] actions = HTTPRequests.get("api/action?host=" + TestActions.TEST_ACTION.host, Action[].class);
        boolean exists = false;
        for (Action action : actions)
            if (TestActions.TEST_ACTION.equals(action)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Action was updated successfully");
    }

    @Test
    @Order(3)
    public void testDeleteAction() throws IOException {
        Action deletedAction = HTTPRequests.deleteWithReturn("api/action", TestActions.TEST_ACTION, Action.class);
        assertNotNull(deletedAction, "Action was been deleted successfully");
        // Make sure the action was deleted
        Action[] actions = HTTPRequests.get("api/action?action=" + TestActions.TEST_ACTION.action, Action[].class);
        boolean exists = false;
        for (Action action : actions)
            if (TestActions.TEST_ACTION.equals(action)) {
                exists = true;
                break;
            }
        assertFalse(exists, "Action was successfully deleted");
    }
}
