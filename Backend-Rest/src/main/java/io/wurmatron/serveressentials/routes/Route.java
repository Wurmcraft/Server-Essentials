package io.wurmatron.serveressentials.routes;


import io.javalin.core.security.Role;
import io.javalin.http.Handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used by routes to automatically register the given route with the provided parameters
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Route {

    /**
     * Path used by javalin for the creation of a route
     * <p>
     * /account/:id
     * /account
     *
     * @see io.javalin.Javalin#get(String, Handler)
     */
    String path();

    /**
     * HTTP method to be used for this route
     */
    String method();

    /**
     * Minimum Role required to access any part of this resource
     */
    RestRoles[] roles() default {RestRoles.ANONYMOUS};

    /**
     * Used for each route to determine if the requested data is accessible to the given user, or some needs to be removed / blanked out
     */
    public enum RestRoles implements Role {
        ANONYMOUS, USER,SERVER, DEV
    }

}
