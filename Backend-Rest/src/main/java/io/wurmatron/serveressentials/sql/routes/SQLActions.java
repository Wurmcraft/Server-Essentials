package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Action;
import io.wurmatron.serveressentials.sql.SQLGenerator;

import javax.annotation.Nullable;
import java.util.*;

public class SQLActions extends SQLGenerator {

    public static String ACTIONS_TABLE = "actions";

    /**
     * Creates a new action to be saved in the db
     *
     * @param action instance of the action to be created
     * @return the created instance that was added to the db
     */
    // TODO Implement
    @Nullable
    public static Action create(Action action) {
        return null;
    }

    /**
     * Update a given action, with the provided values
     *
     * @param action action to pull the updated values from
     * @return updated instance of the action
     */
    // TODO Implement
    @Nullable
    public static Action update(Action action, String[] columnsToUpdate) {
        return null;
    }

    /**
     * Gets a array / list of the actions related to the provided user
     *
     * @param relatedID id of the provided user or channel
     * @return a list of the action related to the provided id
     */
    // TODO Implement
    public static List<Action> get(String relatedID) {
        return null;
    }

    /**
     * Gets a array / list of actions related to the provided user with filtered actions
     *
     * @param relatedID id of the provided user or channel
     * @param action    name of the action that occurred
     * @return a list of all the actions related to the provided user, based on its action
     */
    public static List<Action> get(String relatedID, String action) {
        return null;
    }

    /**
     * Gets a array / list of actions related to the requested criteria
     *
     * @param host      name of the controller, discord bot or server
     * @param action    name of the action that has occurred
     * @param relatedID id of the user involved in the action, discordID or uuid
     * @return a list of all the actions related to the provided details
     */
    // TODO Implement
    public static List<Action> get(String host, String action, String relatedID) {
        return null;
    }

    /**
     * Deletes an instance of a action from the db
     *
     * @param host      name of the controller, discord bot or server
     * @param action    name of the action that occurred
     * @param relatedID id of the user involved in the action, discordID or uuid
     * @param timestamp unix timestamp of when the action was created / happened
     * @return instance of the deleted action
     */
    // TODO Implement
    public static Action delete(String host, String action, String relatedID, long timestamp) {
        return null;
    }
}
