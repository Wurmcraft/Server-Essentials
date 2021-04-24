package io.wurmatron.serveressentials.routes;

import io.javalin.core.security.Role;

/**
 * Used for each route to determine if the requested data is accessible to the given user, or some needs to be removed / blanked out
 */
public enum RestRoles implements Role {
    ANONYMOUS, USER,SERVER, DEV
}
