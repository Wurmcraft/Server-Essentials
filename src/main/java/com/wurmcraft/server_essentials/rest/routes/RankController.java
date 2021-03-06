package com.wurmcraft.server_essentials.rest.routes;

import com.google.gson.JsonSyntaxException;
import com.wurmcraft.server_essentials.rest.SE_Rest;
import com.wurmcraft.server_essentials.rest.api.NetworkUser;
import com.wurmcraft.server_essentials.rest.api.Rank;
import com.wurmcraft.server_essentials.rest.sql.ParamChecker;
import com.wurmcraft.server_essentials.rest.sql.SQLCommands;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static com.wurmcraft.server_essentials.rest.SE_Rest.GSON;
import static com.wurmcraft.server_essentials.rest.SE_Rest.LOG;

public class RankController {

    public static String[] RANK_TABLE_COLUMS = {"name", "permission", "inheritance"};

    @OpenApi(
            description = "Create a new rank",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Rank.class)),
            responses = {
                    @OpenApiResponse(status = "201", description = "New Rank Created, Added to DB"),
                    @OpenApiResponse(status = "400", description = "Invalid/Incomplete Json"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "409", description = "Rank exists"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            },
            tags = {"Ranks"}
    )
    public static Handler addRank = ctx -> {
        try {
            Rank rank = GSON.fromJson(ctx.body(), Rank.class);
            if (rank.name.equalsIgnoreCase(ParamChecker.sanitizeRankName(rank.name)) && rank.name != null) {
                rank.name = ParamChecker.sanitizeRankName(rank.name);
                if (rank.name == null || rank.name.isEmpty()) {
                    ctx.status(422).result("{\"title\": \"Invalid rank name!\", \"status\": 422, \"type\": \"\", \"details\": []}");
                    return;
                }
                // Check for existing
                Rank dbRank = SQLCommands.getRankByName(rank.name);
                if (dbRank == null) {
                    try {
                        SQLCommands.addRank(rank);
                        ctx.status(201);
                    } catch (Exception e) {
                        LOG.error("rank#addRank: " + ctx.body() + " => " + e.getLocalizedMessage());
                        ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
                    }
                } else {
                    ctx.status(409).result("{\"title\": \"Rank with name (\"" + rank.name + "\") exists!\", \"status\": 409, \"type\": \"\", \"details\": []}");
                }
            } else {
                ctx.status(422).result("{\"title\": \"Invalid rank name!\", \"status\": 422, \"type\": \"\", \"details\": []}");
            }
        } catch (JsonSyntaxException e) {
            ctx.status(400).result("{\"title\": \"Invalid Json\", \"status\": 409, \"type\": \"\", \"details\": []}");
        }
    };

    @OpenApi(
            description = "Get a given rank by name",
            responses = {
                    @OpenApiResponse(status = "200", description = "Get requested rank data", content = @OpenApiContent(from = Rank.class)),
                    @OpenApiResponse(status = "400", description = "Invalid Rank"),
                    @OpenApiResponse(status = "404", description = "Requested Rank does not exist!"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            },
            tags = {"Ranks"}
    )
    public static Handler getRank = ctx -> {
        String name = ParamChecker.sanitizeRankName(ctx.pathParam("name"));
        if (!name.isEmpty()) {
            try {
                Rank rank = SQLCommands.getRankByName(name);
                if (rank != null) {
                    ctx.status(200).result(GSON.toJson(rank));
                } else {
                    ctx.status(404).result("{\"title\": \"Rank (\"" + name + "\") not found!\", \"status\": 404, \"type\": \"\", \"details\": []}");
                }
            } catch (SQLException e) {
                LOG.error("rank#getRank: " + ctx.body() + " => " + e.getLocalizedMessage());
                ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
            }
        } else {
            ctx.status(400).result("{\"title\": \"Invalid Rank Name\", \"status\": 400, \"type\": \"\", \"details\": []}");
        }
    };


    @OpenApi(
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Rank.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Rank Updated, DB Updated"),
                    @OpenApiResponse(status = "400", description = "Invalid/Incomplete Json"),
                    @OpenApiResponse(status = "400", description = "Invalid/Incomplete Json"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "404", description = "Rank does not exist!"),
                    @OpenApiResponse(status = "422", description = "Specify fields to update"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            },
            queryParams = {
                    @OpenApiParam(name = "permission", type = Boolean.class),
                    @OpenApiParam(name = "inheritance", type = Boolean.class)
            },
            tags = {"Ranks"}
    )
    public static Handler updateRank = ctx -> {
        try {
            String name = ParamChecker.sanitizeRankName(ctx.pathParam("name"));
            Rank dbRank = SQLCommands.getRankByName(name);
            if (dbRank != null) {
                Map<String, List<String>> queryParams = ctx.queryParamMap();
                if (!queryParams.isEmpty()) {
                    try {
                        Rank updateRank = GSON.fromJson(ctx.body(), Rank.class);
                        StringBuilder builder = new StringBuilder();
                        builder.append("UPDATE `ranks` SET ");
                        for (String key : RANK_TABLE_COLUMS) {
                            if (key.equals("name")) {
                                continue;
                            }
                            if (queryParams.containsKey(key.toLowerCase()) && ParamChecker.returnBool(queryParams.get(key.toLowerCase()).get(0))) {
                                builder.append("`%TYPE%`='%VALUE%', ".replaceAll("%TYPE%", key).replaceAll("%VALUE%", ParamChecker.getRankInfo(updateRank, key)));
                            }
                        }
                        builder.append("WHERE name='%NAME%';".replaceAll("%NAME%", name));
                        String query = builder.toString();
                        query = query.replaceAll(", WHERE", " WHERE");  // Remove trailing
                        try {
                            Statement statement = SE_Rest.connector.getConnection().createStatement();
                            statement.execute(query);
                            ctx.status(200).result(GSON.toJson(SQLCommands.getRankByName(name)));
                        } catch (Exception e) {
                            LOG.error("rank#updateRank: " + ctx.body() + " => " + e.getLocalizedMessage());
                            ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
                        }
                    } catch (JsonSyntaxException e) {
                        ctx.status(400).result("{\"title\": \"Invalid Rank Json\", \"status\": 409, \"type\": \"\", \"details\": []}");
                    }
                } else {
                    ctx.status(422).result("{\"title\": \"Rank (\"" + name + "\") was not updated, you must specify which fields to update!\", \"status\": 422, \"type\": \"\", \"details\": []}");
                }
            } else {
                ctx.status(404).result("{\"title\": \"Rank (\"" + ctx.pathParam("name") + "\") not found!\", \"status\": 404, \"type\": \"\", \"details\": []}");
            }
        } catch (SQLException e) {
            LOG.error("rank#updateRank: " + ctx.body() + " => " + e.getLocalizedMessage());
            ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
        }
    };

    @OpenApi(
            description = "Delete the given rank by name",
            responses = {
                    @OpenApiResponse(status = "200", description = "Rank has been removed from the DB"),
                    @OpenApiResponse(status = "404", description = "Rank does not exist"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "422", description = "Invalid Name"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            },
            tags = {"Ranks"}
    )
    public static Handler deleteRank = ctx -> {
        try {
            String name = ParamChecker.sanitizeUUID(ctx.pathParam("name"));
            if (!name.isEmpty()) {
                Rank rank = SQLCommands.getRankByName(name);
                if (rank != null) {
                    String query = "DELETE FROM `ranks` WHERE `name`='" + name + "';";
                    try {
                        Statement statement = SE_Rest.connector.getConnection().createStatement();
                        statement.execute(query);
                        ctx.status(200);
                    } catch (SQLException e) {
                        LOG.error("rank#deleteRank: " + ctx.body() + " => " + e.getLocalizedMessage());
                        ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
                    }
                } else {
                    ctx.status(404).result("{\"title\": \"Rank (\"" + name + "\") not found!\", \"status\": 404, \"type\": \"\", \"details\": []}");
                }
            } else {
                ctx.status(422).result("{\"title\": \"" + name + "is not a valid name!\", \"status\": 422, \"type\": \"\", \"details\": []}");
            }
        } catch (Exception e) {
            LOG.error("rank#deleteRank: " + ctx.body() + " => " + e.getLocalizedMessage());
            ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
        }
    };

    @OpenApi(
            responses = {
                    @OpenApiResponse(status = "200", description = "Get a list of all ranks (with the requested values", content = @OpenApiContent(from = Rank[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            },
            queryParams = {
                    @OpenApiParam(name = "permission", type = Boolean.class),
                    @OpenApiParam(name = "inheritance", type = Boolean.class)
            },
            tags = {"Ranks"}
    )
    public static Handler getRanks = ctx -> {
        try {
            Map<String, List<String>> queryParams = ctx.queryParamMap();
            Rank[] ranks = SQLCommands.getRanks();
            for (String key : RANK_TABLE_COLUMS) {
                if (key.equals("uuid")) {
                    continue;
                }
                if (!queryParams.containsKey(key.toLowerCase()) || !ParamChecker.returnBool(queryParams.get(key.toLowerCase()).get(0))) {
                    for (Rank rank : ranks) {
                        if (key.equalsIgnoreCase("permission")) {
                            rank.permission = null;
                        } else if (key.equalsIgnoreCase("inheritance")) {
                            rank.inheritance = null;
                        }
                    }
                }
            }
            ctx.status(200).result(GSON.toJson(ranks));
        } catch (Exception e) {
            LOG.error("ranker#getRanks: " + ctx.body() + " => " + e.getLocalizedMessage());
            ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
        }
    };
}
