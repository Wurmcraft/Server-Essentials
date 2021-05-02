package io.wurmatron.serveressentials.routes;

import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.Handler;
import io.wurmatron.serveressentials.utils.HttpUtils;
import org.reflections8.Reflections;
import org.reflections8.scanners.FieldAnnotationsScanner;
import org.reflections8.scanners.MethodAnnotationsScanner;
import org.reflections8.scanners.SubTypesScanner;

import java.lang.reflect.Field;
import java.util.*;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class RouteLoader {

    public static final String[] SUPPORTED_ROUTE_METHODS = {"GET", "POST", "PUT", "DELETE", "PATCH", "BEFORE"};
    public static final Reflections REFLECTIONS = new Reflections(RouteLoader.class.getPackage().getName(), new MethodAnnotationsScanner(), new SubTypesScanner(), new FieldAnnotationsScanner());

    /**
     * Classpath lookup to find all the fields annotated with @Route and validate, if so add the route
     *
     * @return list of all the found valid routes
     */
    private static List<Field> findRoutes() {
        List<Field> validRoutes = new ArrayList<>();
        Set<Field> fields = REFLECTIONS.getFieldsAnnotatedWith(Route.class);
        for (Field field : fields)
            if (isValidRoute(field)) {
                Route route = field.getDeclaredAnnotation(Route.class);
                LOG.debug("Route '" + field.getName() + "' " + route.method() + " (" + route.path() + ") has been created!");
                validRoutes.add(field);
            } else
                LOG.debug("Route '" + field.getName() + " " + field.getClass().toGenericString() + "' is invalid, it will not be registered!");
        return validRoutes;
    }

    /**
     * Checks if the string is a valid route
     *
     * @param method method type provided by the @Route
     * @return if its string is a valid route or not
     */
    private static boolean isValidMethod(String method) {
        method = method.toUpperCase();
        for (String m : SUPPORTED_ROUTE_METHODS)
            if (m.equals(method))
                return true;
        return false;
    }


    /**
     * Checks if a route is valid
     * 1. Route has a supported method type
     * 2. Route has a at least 1 role
     *
     * @param field route field to validate
     * @return if the route is a valid route or not
     */
    public static boolean isValidRoute(Field field) {
        Route routeAnnotation = field.getAnnotation(Route.class);
        if (routeAnnotation == null)
            return false;
        if (!isValidMethod(routeAnnotation.method()))
            return false;
        return routeAnnotation.roles().length > 0 || routeAnnotation.method().equalsIgnoreCase("BEFORE");
    }

    /**
     * Automatically locates all @Route within this package and registers them with javalin
     *
     * @param javalin instance of the router
     */
    public static void registerRoutes(Javalin javalin) {
        List<Field> routes = findRoutes();
        routes.forEach(field -> {
            try {
                register(javalin, field);
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error("Failed to register '" + field.getName() + "' (" + field.getAnnotation(Route.class).path() + ")");
                LOG.debug(e.getMessage());
            }
        });
        LOG.info(routes.size() + " routes have been created!");
        RouteUtils.setupExceptions(javalin);
        LOG.info("Default Exception Handlers have been registered!");
        // Patch JDK Bug
        HttpUtils.allowMethods("PATCH");
    }

    /**
     * Registers the route with javalin, based on the @Route configuration
     *
     * @param javalin instance of the route manager
     * @param field   field / instance of the route
     * @throws InstantiationException Unable to access the field's class
     * @throws IllegalAccessException Unable to access the field's class
     */
    private static void register(Javalin javalin, Field field) throws InstantiationException, IllegalAccessException {
        Route route = field.getAnnotation(Route.class);
        Set<Role> roles = new HashSet<>(Arrays.asList(route.roles()));
        Handler handler = (Handler) field.get(field.getClass());
        switch (route.method().toUpperCase()) {
            case "GET": {
                javalin.get(route.path(), handler, roles);
                break;
            }
            case "POST": {
                javalin.post(route.path(), handler, roles);
                break;
            }
            case "PUT": {
                javalin.put(route.path(), handler, roles);
                break;
            }
            case "DELETE": {
                javalin.delete(route.path(), handler, roles);
                break;
            }
            case "PATCH": {
                javalin.patch(route.path(), handler, roles);
                break;
            }
            case "BEFORE": {
                javalin.before(route.path(), handler);
                break;
            }
        }
    }

}
