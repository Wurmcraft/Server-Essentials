package io.wurmatron.serveressentials.routes.informational;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.ServerStatus;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class StatusRoutes {

    public static NonBlockingHashMap<String, ServerStatus> lastServerStatus = new NonBlockingHashMap<>();

    @OpenApi(
            summary = "Update status of a server",
            description = "Update other server / services about server status / players / errors etc..",
            tags = {"Informational"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = ServerStatus.class)}, required = true, description = "Information about the current server"),
            responses = {
                    @OpenApiResponse(status = "200", description = "Server Status has been updated"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/information/status", method = "POST", roles = {Route.RestRoles.SERVER})
    public static Handler updateStatus = ctx -> {
        try {
            ServerStatus status = GSON.fromJson(ctx.body(), ServerStatus.class);
            lastServerStatus.put(status.serverID, status);
        } catch (JsonParseException e) {
            ctx.status(500).result(response("Bad Json", "Unable to parse json for Update Status!"));
        }
    };

    @OpenApi(
            summary = "Get status of the servers on the network",
            description = "Get the status information from the other servers in the network",
            tags = {"Informational"},
            responses = {
                    @OpenApiResponse(status = "200", description = "Server Status's for all the servers", content = {@OpenApiContent(from = ServerStatus[].class)}),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/information/status", method = "GET")
    public static Handler getStatus = ctx -> {
        if (lastServerStatus.size() > 0) {
            ServerStatus[] statusList = filterBasedOnPerms(ctx, lastServerStatus.values().toArray(new ServerStatus[0]));
            ctx.status(200).result(GSON.toJson(statusList));
        } else
            ctx.status(200).result("[]");
    };

    private static ServerStatus[] filterBasedOnPerms(Context ctx, ServerStatus[] status) {
        Route.RestRoles role = EndpointSecurity.getRole(ctx);
        if (role.equals(Route.RestRoles.DEV) || role.equals(Route.RestRoles.SERVER))
            return status;
        List<ServerStatus> statusArr = new ArrayList<>();
        for (ServerStatus st : status) {
            if ((st.lastUpdate + ServerEssentialsRest.config.server.cacheTime) < Instant.now().getEpochSecond())
                lastServerStatus.remove(st.serverID);
            if (role.equals(Route.RestRoles.USER)) {
                // TODO Check for System Perms
                st.specialData = null;
                statusArr.add(st);
            } else if (role.equals(Route.RestRoles.ANONYMOUS)) {
                st.lastUpdate = null;
                st.specialData = null;
                st.playerInfo = null;
                statusArr.add(st);
            }
        }
        return statusArr.toArray(new ServerStatus[0]);
    }
}
