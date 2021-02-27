package com.wurmcraft.server_essentials.rest.sql;

import com.wurmcraft.server_essentials.rest.SE_Rest;
import com.wurmcraft.server_essentials.rest.api.NetworkUser;
import joptsimple.internal.Strings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLCommands {

    public static NetworkUser getUserByUUID(String uuid) throws SQLException {
        return getUser("uuid", uuid);
    }

    public static NetworkUser getUserByName(String username) throws SQLException {
        return getUser("username", username);
    }

    public static NetworkUser getUser(String type, String data) throws SQLException {
        Statement statement = SE_Rest.connector.getConnection().createStatement();
        String query = "SELECT * FROM users WHERE uuid='%TYPE%' LIMIT 1;".replaceAll("%TYPE%", data).replaceAll("uuid", type);
        ResultSet result = statement.executeQuery(query);
        if (result.next()) {
            try {
                return getUserFromResults(result);
            } catch (SQLException e) {
                throw e;
            }
        }
        return null;
    }

    public static void addUser(NetworkUser user) throws SQLException {
        Statement statement = SE_Rest.connector.getConnection().createStatement();
        String query = "INSERT INTO `users` (`uuid`, `username`, `rank`) VALUES ('%UUID%','%USERNAME%', '%RANK%')".replace("%UUID%", user.uuid).replace("%USERNAME%", user.username).replace("%RANK%", Strings.join(user.rank, " "));
        statement.execute(query);
    }

    public static NetworkUser[] getUsers() throws SQLException {
        List<NetworkUser> users = new ArrayList<>();
        Statement statement = SE_Rest.connector.getConnection().createStatement();
        String query = "SELECT * FROM users";
        ResultSet result = statement.executeQuery(query);
        while(result.next()) {
            users.add(getUserFromResults(result));
        }
        return users.toArray(new NetworkUser[0]);
    }

    private static NetworkUser getUserFromResults(ResultSet set) throws SQLException {
        NetworkUser user = new NetworkUser();
        user.uuid = set.getString("uuid");
        user.username = set.getString("username");
        user.rank = set.getString("rank").split(" ");
        return user;
    }
}
